package com.uhdyl.backend.global.config.webSocket;

import com.uhdyl.backend.chat.service.ChatRoomService;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.jwt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtHandler jwtHandler;
    private final ChatRoomService chatRoomService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        switch (accessor.getCommand()) {

            case CONNECT -> {
                String authToken = accessor.getFirstNativeHeader("Authorization");

                // 연결 시점에 헤더에서 토큰 추출 및 검증
                if (authToken != null && authToken.startsWith("Bearer ")) {
                    String token = authToken.substring(7);

                    try{
                        JwtUserClaim claims = jwtHandler.parseToken(token);
                        JwtAuthentication jwtAuthentication = new JwtAuthentication(claims);
                        accessor.setUser(jwtAuthentication);
                    }
                    catch (Exception e){
                        throw new BusinessException(ExceptionType.WS_TOKEN_INVALID);
                    }
                }
                else {
                    throw new BusinessException(ExceptionType.WS_TOKEN_MISSING);
                }
            }

            case SUBSCRIBE -> {
                String destination = accessor.getDestination();
                if(destination != null && destination.startsWith("/sub/chat/")){
                    validateParticipant(destination, accessor);
                }
                accessor.setDestination(destination);
            }

            case SEND -> {
                String destination = accessor.getDestination();
                if(destination != null && destination.startsWith("/pub/chat/room")){
                    validateParticipant(destination, accessor);
                }
            }

        }
        return message;
    }

    public void validateParticipant(String destination, StompHeaderAccessor accessor){
        int last = destination.lastIndexOf('/');

        if (last == -1 || last == destination.length() - 1) {
            throw new BusinessException(ExceptionType.WS_INVALID_ROOM_PATH);
        }

        Long roomId;
        try {
            roomId = Long.parseLong(destination.substring(last + 1));
        } catch (NumberFormatException e) {
            throw new BusinessException(ExceptionType.WS_INVALID_ROOM_PATH);
        }

        Principal user = accessor.getUser();
        if (user == null)
            throw new BusinessException(ExceptionType.WS_TOKEN_MISSING);


        if(user instanceof JwtAuthentication){
            Long userId = ((JwtAuthentication) user).userId();
            if(!chatRoomService.isParticipant(roomId, userId))
                throw new BusinessException(ExceptionType.WS_ROOM_ACCESS_DENIED);
        }
    }

}

