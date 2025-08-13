package com.uhdyl.backend.product.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.image.domain.QImage;
import com.uhdyl.backend.product.domain.QProduct;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductListResponse;
import com.uhdyl.backend.product.dto.response.SalesStatsResponse;
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
                        product.price,
                        product.user.name,
                        image.imageUrl.min(),
                        product.isSale.not()
                ))
                .from(product)
                .leftJoin(product.images,image)
                .where(product.user.id.eq(userId))
                .groupBy(product.id, product.name, product.price, product.user.name, product.isSale, image.imageOrder)
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

        Tuple result = jpaQueryFactory
                .select(user.name, product.count(), product.price.sumLong())
                .from(user)
                .leftJoin(user.products, product)
                .on(product.isSale.eq(false))
                .where(user.id.eq(userId))
                .fetchOne();

        String name = result != null ? result.get(user.name) : null;
        Long salesCount = result != null ? result.get(1, Long.class) : 0L;
        Long salesRevenue = result != null ? result.get(2, Long.class) : 0L;

        return new SalesStatsResponse(
                name != null ? name : "",
                salesCount != null ? salesCount : 0L,
                salesRevenue != null ? salesRevenue : 0L
        );
    }
}
