package com.uhdyl.backend.chat.controller;

import com.uhdyl.backend.chat.dto.request.ChatRoomRequest;
import com.uhdyl.backend.chat.dto.response.ChatRoomResponse;
import com.uhdyl.backend.chat.service.ChatRoomService;
import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/chat/room")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public ResponseEntity<ResponseBody<ChatRoomResponse>> createChatRoom(
            // TODO: 현재는 상대방의 ID를 전달받지만, 프론트 연결 시에는 상품의 PK 또는 상품의 제목과 닉네임을 받아서 채팅방을 만드는 구조로 변경해야됨
            @RequestBody ChatRoomRequest request,
            Long userId
    ){
        return ResponseEntity.ok(createSuccessResponse(chatRoomService.createChatRoom(request.opponentId(), userId)));
    }
}
