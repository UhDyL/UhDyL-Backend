package com.uhdyl.backend.chat.controller;

import com.uhdyl.backend.chat.api.ChatRoomApi;
import com.uhdyl.backend.chat.dto.request.ChatRoomRequest;
import com.uhdyl.backend.chat.dto.response.ChatRoomResponse;
import com.uhdyl.backend.chat.service.ChatRoomService;
import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController implements ChatRoomApi {

    private final ChatRoomService chatRoomService;

    @PostMapping("/chat/room")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ChatRoomResponse>> createChatRoom(
            @RequestBody ChatRoomRequest request,
            Long userId
    ){
        return ResponseEntity.ok(createSuccessResponse(chatRoomService.createChatRoom(request, userId)));
    }

    @GetMapping("/chat/room")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ChatRoomResponse>>> getChatRooms(
            Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(createSuccessResponse(chatRoomService.getChatRooms(userId, pageable)));
    }

    @PostMapping("/chat/room/complete")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    public ResponseEntity<ResponseBody<Void>> completeTrade(
            Long userId,
            @RequestParam Long chatRoomId
    ){
        chatRoomService.completeTrade(userId, chatRoomId);
        return ResponseEntity.ok(createSuccessResponse());
    }
}
