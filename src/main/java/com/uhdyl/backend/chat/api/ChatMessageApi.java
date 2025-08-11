package com.uhdyl.backend.chat.api;

import com.uhdyl.backend.chat.dto.request.ChatMessageRequest;
import com.uhdyl.backend.chat.dto.response.ChatMessageResponse;
import com.uhdyl.backend.chat.dto.response.ChatRoomResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.jwt.JwtAuthentication;
import com.uhdyl.backend.global.response.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "채팅 메시지 API", description = "채팅 메시지 관련 API")
public interface ChatMessageApi {
    @MessageMapping("/chat/room/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequest request,
            JwtAuthentication authentication
    );

    @Operation(
            summary = "채팅 메시지 페이징 조회",
            description = "채팅 메시지를 페이징 조회합니다. 특정 시간 이전의 채팅 메시지를 불러오려면 특정 시간을 함께 넘겨줘야 합니다." +
                    "(드래그를 통해 이전 메시지를 불러와야할 경우 가장 예전 메시지의 시간을 함께 전달)"
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ChatMessageResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ChatMessageResponse.class,
                    description = "채팅 메시지 페이징 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.CHATROOM_NOT_EXIST),
                    @SwaggerApiFailedResponse(ExceptionType.WS_ROOM_ACCESS_DENIED)

            }
    )
    @GetMapping("/chat/room/{roomId}/messages")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Page<ChatMessageResponse>>> findChatMessages(
            @PathVariable Long roomId,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable,
            @RequestParam(required = false) LocalDateTime startDateTime
    );


    @Operation(
            summary = "WebSocket 채팅 전송",
            description = """
                    STOMP SEND /pub/chat/room/{roomId} 로 메시지 전송합니다.
                    이 엔드포인트는 더미 엔드포인트입니다. 이 엔드포인트를 스웨거에서 테스트할 수 없습니다.
                    /sub/chat/{roomId}의 경로를 구독하여 메시지를 수신할 수 있습니다.""")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ChatMessageResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ChatMessageResponse.class,
                    description = "채팅 메시지 페이징 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.CHATROOM_NOT_EXIST)
            }
    )
    @PostMapping("/swagger-docs/ws/chat/room/{roomId}")
    public void swaggerSendMessage(
            @PathVariable Long roomId,
            @RequestBody ChatMessageRequest request
    );
}
