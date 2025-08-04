package com.uhdyl.backend.chat.service;

import com.uhdyl.backend.chat.domain.ChatRoom;
import com.uhdyl.backend.chat.dto.response.ChatRoomResponse;
import com.uhdyl.backend.chat.repository.ChatRoomRepository;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;


    @Transactional
    public ChatRoomResponse createChatRoom(Long opponentId, Long userId){
        // 이미 존재하는 채팅방이 있는지 확인 후,
        // 이미 존재한다면 그 채팅방과 채팅 메시지를 반환
        // 없다면 새로 채팅방을 만들어서 반환

        // TODO: 프론트 연결 시 상품의 제목과 아이디를 통해 유저를 DB에서 조회하는 과정이 필요함
        //  Product product = productRepository.findByTitleAndName(title,name);
        //  product.getUser()?

        Long user1 = Math.min(userId, opponentId);
        Long user2 = Math.max(userId, opponentId);

        if(Objects.equals(user1, user2))
            throw new BusinessException(ExceptionType.CANT_CREATE_CHATROOM);

        return chatRoomRepository.findByUser1AndUser2(user1, user2)
                .map(
                        ChatRoomResponse::to)
                .orElseGet(
                        () -> {
                            return ChatRoomResponse.to(
                                    chatRoomRepository.save(
                                            ChatRoom.builder()
                                                    .user1(user1)
                                                    .user2(user2)
                                                    .build()
                                            ));

                        }
                );
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
}
