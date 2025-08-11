package com.uhdyl.backend.chat.api;

import com.uhdyl.backend.chat.dto.request.ChatRoomRequest;
import com.uhdyl.backend.chat.dto.response.ChatRoomResponse;
import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "채팅방 API", description = "채팅방 관련 API")
public interface ChatRoomApi {
    @Operation(
            summary = "채팅방 생성",
            description = "구매자는 판매자와의 채팅방을 생성합니다."
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ChatRoomResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ChatRoomResponse.class,
                    description = "채팅방 생성 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.CANT_CREATE_CHATROOM)
            }
    )
    @PostMapping("/chat/room")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ChatRoomResponse>> createChatRoom(
            // TODO: 현재는 상대방의 ID를 전달받지만, 프론트 연결 시에는 상품의 PK 또는 상품의 제목과 닉네임을 받아서 채팅방을 만드는 구조로 변경해야됨
            @RequestBody ChatRoomRequest request,
            @Parameter(hidden = true) Long userId
    );
}
