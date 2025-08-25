package com.uhdyl.backend.chat.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uhdyl.backend.chat.domain.ChatRoom;
import com.uhdyl.backend.chat.domain.QChatMessage;
import com.uhdyl.backend.chat.domain.QChatRoom;
import com.uhdyl.backend.chat.dto.response.ChatRoomResponse;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.image.domain.QImage;
import com.uhdyl.backend.product.domain.QProduct;
import com.uhdyl.backend.product.dto.response.ProductListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CustomChatRoomRepositoryImpl implements CustomChatRoomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GlobalPageResponse<ChatRoomResponse> getChatRooms(Long userId, Pageable pageable) {
        QChatRoom qChatRoom = QChatRoom.chatRoom;
        QProduct qProduct = QProduct.product;
        QImage qImage = QImage.image;
        QChatMessage qChatMessage = QChatMessage.chatMessage;
        QChatMessage cm = new QChatMessage("cm");

        BooleanBuilder builder = new BooleanBuilder()
                .and(qChatRoom.user1.eq(userId).or(qChatRoom.user2.eq(userId)));

        Expression<String> mainImageUrl = JPAExpressions
                .select(qImage.imageUrl.min())
                .from(qImage)
                .where(qImage.in(qProduct.images));

        var latestMsgId = JPAExpressions
                .select(cm.id.max())
                .from(cm)
                .where(cm.chatRoom.id.eq(qChatRoom.id));

        List<ChatRoomResponse> response = jpaQueryFactory
                .select(Projections.constructor(
                        ChatRoomResponse.class,
                        qChatRoom.id,
                        qChatRoom.chatRoomTitle,
                        Projections.constructor(
                                ProductListResponse.class,
                                qProduct.id,
                                qProduct.title,
                                qProduct.price,
                                qProduct.user.name,
                                qProduct.user.picture,
                                mainImageUrl,
                                qProduct.isSale
                        ),
                        qChatMessage.message,
                        qChatMessage.createdAt,
                        qChatRoom.tradeCompleted
                ))
                .from(qChatRoom)
                .leftJoin(qProduct).on(qProduct.id.eq(qChatRoom.productId))
                .leftJoin(qChatMessage).on(
                        qChatRoom.id.eq(qChatMessage.chatRoom.id)
                                .and(qChatMessage.id.eq(latestMsgId))
                )
                .where(builder)
                .orderBy(qChatMessage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(qChatRoom.count())
                .from(qChatRoom)
                .where(builder)
                .fetchOne();

        return GlobalPageResponse.create(new PageImpl<>(response, pageable, total == null ? 0 : total));
    }

}
