package com.uhdyl.backend.chat.service;

import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.domain.ChatRoom;
import com.uhdyl.backend.chat.dto.request.ChatRoomRequest;
import com.uhdyl.backend.chat.dto.response.ChatRoomResponse;
import com.uhdyl.backend.chat.repository.ChatMessageRepository;
import com.uhdyl.backend.chat.repository.ChatRoomRepository;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.product.repository.ProductRepository;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomRequest request, Long userId){

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(ExceptionType.PRODUCT_NOT_FOUND));
        Long opponentId = product.getUser().getId();
        Long user1 = Math.min(userId, opponentId);
        Long user2 = Math.max(userId, opponentId);

        if(Objects.equals(user1, user2))
            throw new BusinessException(ExceptionType.CANT_CREATE_CHATROOM);

        ChatRoom room = chatRoomRepository
                .findByUser1AndUser2AndProductId(user1, user2, request.productId())
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder()
                                .user1(user1)
                                .user2(user2)
                                .chatRoomTitle(product.getTitle())
                                .productId(request.productId())
                                .build()
                ));

        ChatMessage last = chatMessageRepository
                .findFirstByChatRoom_IdOrderByCreatedAtDescIdDesc(room.getId())
                .orElse(null);

        return ChatRoomResponse.to(room, product, last);
    }

    public boolean isParticipant(Long roomId, Long userId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ExceptionType.CHATROOM_NOT_EXIST));

        boolean isUser;
        if (chatRoom.getUser1().equals(userId) || chatRoom.getUser2().equals(userId))
            isUser = true;
        else
            isUser = false;


        return isUser;
    }

    public GlobalPageResponse<ChatRoomResponse> getChatRooms(Long userId, Pageable pageable){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return chatRoomRepository.getChatRooms(userId, pageable);
    }
}
