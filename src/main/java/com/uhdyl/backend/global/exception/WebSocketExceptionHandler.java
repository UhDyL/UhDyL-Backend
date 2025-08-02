package com.uhdyl.backend.global.exception;

import com.uhdyl.backend.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler(BusinessException.class)
    public void handleBusinessException(BusinessException exception, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.error("WebSocket 비즈니스 예외 발생 - Session: {}, 메시지: {}",
                sessionId,
                exception.getMessage());

        // 에러 발생한 특정 사용자에게만 에러 메시지 전송
        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",  // 클라이언트는 /user/queue/errors를 구독해야 함
                new ErrorMessage(exception.getExceptionType().getCode(),
                        exception.getMessage())
        );
    }

    @MessageExceptionHandler(Exception.class)
    public void handleException(Exception exception, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.error("WebSocket 예상치 못한 예외 발생 - Session: {}, 메시지: {}",
                sessionId,
                exception.getMessage());

        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                new ErrorMessage("WS001", "예상치 못한 에러가 발생했습니다.")
        );
    }

    // 연결 종료 시 로깅
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("웹소켓 연결 종료: {}", sessionId);
    }
}