package com.uhdyl.backend.user.api;


import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.user.dto.request.LocationRequest;
import com.uhdyl.backend.user.dto.response.LocationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "사용자 API", description = "사용자 관련 API")
public interface UserApi {

    @Operation(
            summary = "로그아웃",
            description = "사용자는 로그아웃을 진행합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "로그아웃 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
            }
    )
    @AssignUserId
    @DeleteMapping("/logout")
    @PreAuthorize(" isAuthenticated() and hasAuthority('USER')")
    public ResponseEntity<ResponseBody<Void>> logout(@Parameter(hidden = true) Long userId);

    @Operation(
            summary =  "판매자 밭 위치 저장",
            description = "FARMER 권한을 가진 사용자의 위치를 저장합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "위치 저장 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FARMER)
            }
    )
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    @PostMapping("/location")
    ResponseEntity<ResponseBody<Void>> saveLocation(@RequestBody LocationRequest request,
                                                    @Parameter(hidden = true) Long userId);


    @Operation(
            summary =  "판매자 밭 위치 반환",
            description = "FARMER 권한을 가진 사용자의 위치를 반환합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "위치 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.LOCATION_NOT_FOUND)
            }
    )
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/location")
    ResponseEntity<ResponseBody<LocationResponse>> getLocation(@Parameter(hidden = true) Long userId);
}
