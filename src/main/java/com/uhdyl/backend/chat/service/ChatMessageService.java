package com.uhdyl.backend.chat.service;

import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.domain.ChatRoom;
import com.uhdyl.backend.chat.dto.request.ChatMessageRequest;
import com.uhdyl.backend.chat.dto.response.ChatMessageResponse;
import com.uhdyl.backend.chat.repository.ChatMessageRepository;
import com.uhdyl.backend.chat.repository.ChatRoomRepository;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;

    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, Long userId, Long chatRoomId){
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ExceptionType.CHATROOM_NOT_EXIST));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        if(!chatRoomService.isParticipant(chatRoomId, userId)){
            throw new BusinessException(ExceptionType.WS_ROOM_ACCESS_DENIED);
        }

        request.setMessage(request.getMessage());

        ChatMessage chatMessage = ChatMessage.builder()
                .message(request.getMessage())
                .user(user)
                .imageUrl(request.getImageUrl())
                .chatRoom(chatRoom)
                .publicId(request.getPublicId())
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.to(chatMessage);
    }

    @Transactional(readOnly = true)
    public GlobalPageResponse<ChatMessageResponse> findChatMessages(Long roomId, Pageable pageable, LocalDateTime startDateTime){
        chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ExceptionType.CHATROOM_NOT_EXIST));
        return chatMessageRepository.findChatMessages(roomId, pageable, startDateTime);
    }
}
