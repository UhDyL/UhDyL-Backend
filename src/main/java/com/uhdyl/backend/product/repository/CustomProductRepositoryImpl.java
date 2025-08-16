package com.uhdyl.backend.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.image.domain.QImage;
import com.uhdyl.backend.product.domain.Category;
import com.uhdyl.backend.product.domain.QProduct;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductDetailResponse;
import com.uhdyl.backend.product.dto.response.ProductListResponse;
import com.uhdyl.backend.product.dto.response.SalesStatsResponse;
import com.uhdyl.backend.review.domain.QReview;
import com.uhdyl.backend.user.domain.QUser;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class CustomProductRepositoryImpl implements CustomProductRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public CustomProductRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public MyProductListResponse getMyProducts(Long userId, Pageable pageable){
        QProduct product = QProduct.product;
        QImage image = QImage.image;

        List<ProductListResponse> content = jpaQueryFactory
                .select(Projections.constructor(ProductListResponse.class,
                        product.id,
                        product.name,
                        product.title,
                        product.price,
                        product.user.name,
                        image.imageUrl.min(),
                        product.isSale.not()
                ))
                .from(product)
                .leftJoin(product.images,image)
                .where(product.user.id.eq(userId))
                .groupBy(product.id, product.name, product.title, product.price, product.user.name, product.isSale, image.imageOrder)
                .orderBy(product.createdAt.desc(), image.imageOrder.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(product.user.id.eq(userId))
                .fetchOne();

        Long completedCount = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(product.user.id.eq(userId)
                        .and(product.isSale.eq(false))) // false = 거래완
                .fetchOne();

        Page<ProductListResponse> page = new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0);

        return new MyProductListResponse(
                totalCount != null ? totalCount : 0,
                completedCount != null ? completedCount : 0,
                GlobalPageResponse.create(page)
        );
    }

    @Override
    public SalesStatsResponse getSalesStats(Long userId){
        QProduct product = QProduct.product;
        QUser user = QUser.user;

        SalesStatsResponse stats = jpaQueryFactory
                .select(Projections.constructor(SalesStatsResponse.class,
                        user.name.coalesce(""),
                        product.count(),
                        product.price.sumLong().coalesce(0L)
                ))
                .from(user)
                .leftJoin(user.products, product)
                .on(product.isSale.eq(false))
                .where(user.id.eq(userId))
                .groupBy(user.id, user.name)
                .fetchOne();

        return stats != null ? stats : new SalesStatsResponse("", 0L, 0L);
    }

    @Override
    public GlobalPageResponse<ProductListResponse> getProductsByCategory(Category category, Pageable pageable){
        QProduct product = QProduct.product;
        QImage image = QImage.image;

        List<ProductListResponse> content = jpaQueryFactory
                .select(Projections.constructor(ProductListResponse.class,
                        product.id,
                        product.name,
                        product.title,
                        product.price,
                        product.user.name,
                        image.imageUrl,
                        product.isSale.not()
                ))
                .from(product)
                .leftJoin(product.images, image).on(image.imageOrder.eq(0L))
                .where(product.categories.any().eq(category)
                        .and(product.isSale.eq(true)))
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(product.categories.any().eq(category)
                        .and(product.isSale.eq(true)))
                .fetchOne();

        Page<ProductListResponse> page = new PageImpl<>(content, pageable, total != null ? total : 0);

        return GlobalPageResponse.create(page);
    }

    @Override
    public ProductDetailResponse getProductDetail(Long productId) {
        QProduct product = QProduct.product;
        QImage image = QImage.image;
        QUser user = QUser.user;
        QReview review = QReview.review;

        var productInfoTuple = jpaQueryFactory
                .select(
                        product.id,
                        product.name,
                        product.title,
                        product.price,
                        product.description,
                        user.name,
                        user.picture,
                        JPAExpressions
                                .select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .where(review.targetUserId.eq(user.id)),
                        product.isSale.not()
                )
                .from(product)
                .leftJoin(product.user, user)
                .where(product.id.eq(productId))
                .fetchOne();

        if (productInfoTuple == null) {
            throw new BusinessException(ExceptionType.PRODUCT_NOT_FOUND);
        }

        List<String> images = jpaQueryFactory
                .select(image.imageUrl)
                .from(product)
                .join(product.images, image)
                .where(product.id.eq(productId))
                .orderBy(image.imageOrder.asc())
                .fetch();

        return new ProductDetailResponse(
                productInfoTuple.get(0, Long.class),
                productInfoTuple.get(1, String.class),
                productInfoTuple.get(2, String.class),
                productInfoTuple.get(3, Long.class),
                productInfoTuple.get(4, String.class),
                productInfoTuple.get(5, String.class),
                productInfoTuple.get(6, String.class),
                productInfoTuple.get(7, Double.class),
                images,
                Boolean.TRUE.equals(productInfoTuple.get(8, Boolean.class))
        );
    }
}
