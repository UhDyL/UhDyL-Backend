package com.uhdyl.backend.zzim.api;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.zzim.dto.request.ZzimCreateRequest;
import com.uhdyl.backend.zzim.dto.response.ZzimResponse;
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

@Tag(name = "찜 API", description = "찜 관련 API")
public interface ZzimApi {

    @Operation(
            summary = "찜 등록",
            description = "사용자는 판매 글을 찜으로 등록합니다."
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
    public ResponseEntity<ResponseBody<Void>> createZzim(
            @Parameter(hidden = true) Long userId,
            @RequestBody ZzimCreateRequest request
    );

    @Operation(
            summary = "찜 페이징 조회",
            description = "등록한 찜을 페이징으로 조회합니다."
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ZzimResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    responsePage = ZzimResponse.class,
                    description = "리뷰 페이징 조회 성공"
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

    @Operation(
            summary = "찜 삭제",
            description = "사용자는 자신이 등록한 찜을 삭제할 수 있습니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "리뷰 삭제 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.ZZIM_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.ZZIM_ACCESS_DENIED)
            }
    )
    @DeleteMapping("/zzim/{zzimId}")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> deleteZzim(
            @Parameter(hidden = true) Long userId,
            @PathVariable Long zzimId
    );
}
