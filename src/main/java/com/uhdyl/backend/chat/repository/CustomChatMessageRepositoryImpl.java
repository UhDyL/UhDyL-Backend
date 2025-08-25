package com.uhdyl.backend.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.domain.QChatMessage;
import com.uhdyl.backend.chat.dto.response.ChatMessageResponse;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomChatMessageRepositoryImpl implements CustomChatMessageRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GlobalPageResponse<ChatMessageResponse> findChatMessages(Long roomId, Pageable pageable, LocalDateTime startDateTime) {

        QChatMessage qChatMessage = QChatMessage.chatMessage;

        List<ChatMessage> messages = jpaQueryFactory
                .selectFrom(qChatMessage)
                .leftJoin(qChatMessage.user).fetchJoin()
                .where(
                        qChatMessage.chatRoom.id.eq(roomId),
                        startDateTime != null ? qChatMessage.createdAt.lt(startDateTime) : null
                )
                .orderBy(qChatMessage.createdAt.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long total = jpaQueryFactory
                .select(qChatMessage.count())
                .from(qChatMessage)
                .where(
                        qChatMessage.chatRoom.id.eq(roomId),
                        startDateTime != null ? qChatMessage.createdAt.lt(startDateTime) : null
                )
                .fetchOne();

        List<ChatMessageResponse> response = ChatMessageResponse.to(messages);
        Page<ChatMessageResponse> pageResponse = new PageImpl<>(response, pageable, total != null ? total : 0);
        return GlobalPageResponse.create(pageResponse);
    }

    @Override
    public boolean existsByUserIdAndPublicId(Long userId, String publicId) {

        QChatMessage qChatMessage = QChatMessage.chatMessage;

        return jpaQueryFactory
                .selectFrom(qChatMessage)
                .where(
                        qChatMessage.user.id.eq(userId),
                        qChatMessage.publicId.eq(publicId)
                )
                .fetchOne() != null;
    }
}
