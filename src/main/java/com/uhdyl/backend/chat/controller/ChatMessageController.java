package com.uhdyl.backend.chat.controller;

import com.uhdyl.backend.chat.api.ChatMessageApi;
import com.uhdyl.backend.chat.dto.request.ChatMessageRequest;
import com.uhdyl.backend.chat.dto.response.ChatMessageResponse;
import com.uhdyl.backend.chat.service.ChatMessageService;
import com.uhdyl.backend.global.jwt.JwtAuthentication;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController implements ChatMessageApi {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/room/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId, 
            @Payload ChatMessageRequest request, 
            JwtAuthentication authentication
    ) {
        ChatMessageResponse response = chatMessageService.sendMessage(request, authentication.userId(), roomId);
        ResponseBody<ChatMessageResponse> responseBody = createSuccessResponse(response);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId, responseBody);
    }

    @GetMapping("/chat/room/{roomId}/messages")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ChatMessageResponse>>> findChatMessages(
            @PathVariable Long roomId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable,
            @RequestParam(required = false) LocalDateTime startDateTime
    ) {
        Page<ChatMessageResponse> response = chatMessageService.findChatMessages(roomId, pageable, startDateTime);
        return ResponseEntity.ok(createSuccessResponse(GlobalPageResponse.create(response)));
    }

    @PostMapping("/chat/room/{roomId}")
    public void swaggerSendMessage(
            @PathVariable Long roomId,
            @RequestBody ChatMessageRequest request
    ) {
        // 실제 동작 없음 - Swagger 문서용
    }
}