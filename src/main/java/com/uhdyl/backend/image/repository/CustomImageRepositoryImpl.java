package com.uhdyl.backend.image.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uhdyl.backend.image.domain.QImage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomImageRepositoryImpl implements CustomImageRepository{

    private final JPAQueryFactory jpaQueryFactory;

    // TODO: Product 도메인 개발 후 쿼리 변경하기
    @Override
    public boolean existsByUserIdAndPublicId(Long userId, String publicId) {
        QImage qImage = QImage.image;

        return jpaQueryFactory
                .selectFrom(qImage)
                .where(
                    qImage.publicId.eq(publicId)
                )
                .fetchOne() != null;
    }
}
