package com.uhdyl.backend.zzim.api;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.zzim.dto.request.ZzimToggleRequest;
import com.uhdyl.backend.zzim.dto.response.ZzimResponse;
import com.uhdyl.backend.zzim.dto.response.ZzimToggleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;

@Tag(name = "찜 API", description = "찜 관련 API")
public interface ZzimApi {

    @Operation(
            summary = "찜 토글",
            description = "사용자는 판매 글이 찜으로 등록되어 있으면 삭제합니다. " +
                    "사용자는 판매 글이 찜으로 등록되어 있지 않으면 찜으로 등록합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "찜 등록 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.ALREADY_ZZIMED),
                    @SwaggerApiFailedResponse(ExceptionType.PRODUCT_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.ALREADY_ZZIMED)
            }
    )
    @PostMapping("/zzim")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ZzimToggleResponse>> toggleZzim(
            @Parameter(hidden = true) Long userId,
            @RequestBody ZzimToggleRequest request
    );

    @Operation(
            summary = "찜 페이징 조회",
            description = "등록한 찜을 페이징으로 조회합니다." +
                    "찜으로 등록된 상품을 조회하기 때문에 찜 여부를 나타내는 필드는 표시되지 않습니다."
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ZzimResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    responsePage = ZzimResponse.class,
                    description = "찜 페이징 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
            }
    )
    @GetMapping("/zzim")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ZzimResponse>>> getZzims(
            @Parameter(hidden = true) Long userId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );
}
