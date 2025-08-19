package com.uhdyl.backend.zzim.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.image.domain.QImage;
import com.uhdyl.backend.product.domain.QProduct;
import com.uhdyl.backend.zzim.domain.QZzim;
import com.uhdyl.backend.zzim.dto.response.ZzimResponse;
import com.uhdyl.backend.zzim.dto.response.ZzimToggleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CustomZzimRepositoryImpl implements CustomZzimRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GlobalPageResponse<ZzimResponse> findAllByUser(Long userId, Pageable pageable) {
        QZzim qZzim = QZzim.zzim;
        QProduct qProduct = QProduct.product;
        QImage qImage = QImage.image;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qZzim.user.id.eq(userId));

        List<ZzimResponse> queryResponse = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ZzimResponse.class,
                                qZzim.id,
                                qProduct.id,
                                qProduct.title,
                                qImage.imageUrl.min(),
                                qProduct.price,
                                qProduct.user.name
                        )
                )
                .from(qZzim)
                .innerJoin(qZzim.product, qProduct)
                .leftJoin(qProduct.images, qImage)
                .where(builder)
                .groupBy(qZzim.id, qProduct.id, qProduct.title, qProduct.price, qProduct.user.name)
                .orderBy(qZzim.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(qZzim.count())
                .from(qZzim)
                .where(builder)
                .fetchOne();


        Page<ZzimResponse> pageResponse =  new PageImpl<>(queryResponse, pageable, total != null ? total : 0);
        return GlobalPageResponse.create(pageResponse);
    }

    @Override
    public ZzimResponse findZzim(Long userId, Long productId) {
        QZzim qZzim = QZzim.zzim;
        QProduct qProduct = QProduct.product;
        QImage qImage = QImage.image;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qZzim.user.id.eq(userId).and(qZzim.product.id.eq(productId)));

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ZzimResponse.class,
                                qZzim.id,
                                qProduct.id,
                                qProduct.title,
                                qImage.imageUrl.min(),
                                qProduct.price,
                                qProduct.user.name
                        )
                )
                .from(qZzim)
                .innerJoin(qZzim.product, qProduct)
                .leftJoin(qProduct.images, qImage)
                .where(builder)
                .groupBy(qZzim.id, qProduct.id)
                .fetchOne();

    }


}
