package com.uhdyl.backend.user.api;


import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.token.dto.response.TokenResponse;
import com.uhdyl.backend.user.dto.request.LocationRequest;
import com.uhdyl.backend.user.dto.response.LocationResponse;
import com.uhdyl.backend.user.dto.request.UserNicknameUpdateRequest;
import com.uhdyl.backend.user.dto.request.UserProfileUpdateRequest;
import com.uhdyl.backend.user.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize(" isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> logout(@Parameter(hidden = true) Long userId);

    @Operation(
            summary =  "사용자 밭 위치 저장",
            description = "USER 권한을 가진 사용자의 밭 위치를 저장합니다."
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
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
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


    @Operation(
            summary = "밭 등록 후 판매자 권한(FARMER) 획득",
            description = "사용자는 밭 등록 후 판매자 권한(FARMER)를 획득하고 액세스 토큰과 리프래시 토큰을 재발급 받습니다."
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = TokenResponse.class,
                    description = "판매자 권한(FARMER) 획득 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.BBAT_NOT_UPDATED),
            }
    )
    @AssignUserId
    @PostMapping("/complete-registration")
    @PreAuthorize(" isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<TokenResponse>> completeRegistration(
            @Parameter(hidden = true) Long userId
    );


    @Operation(
            summary =  "닉네임 변경",
            description = "사용자는 초기 닉네임을 변경합니다. 이후의 닉네임 변경은 프로필 정보 수정 api를 통해 진행합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "닉네임 변경 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NICKNAME_DUPLICATED)
            }
    )
    @AssignUserId
    @PostMapping("/nickname")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> updateNickname(
            @Parameter(hidden = true) Long userId,
            @RequestBody UserNicknameUpdateRequest request
    );


    @Operation(
            summary = "사용자 프로필 조회",
            description = "사용자는 자신의 프로필 정보를 조회합니다."
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = UserProfileResponse.class,
                    description = "프로필 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
            }
    )
    @AssignUserId
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<UserProfileResponse>> getProfile(
            @Parameter(hidden = true) Long userId
    );


    @Operation(
            summary = "사용자 프로필 수정",
            description = "사용자는 자신의 프로필 정보를 수정합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "프로필 수정 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NICKNAME_DUPLICATED)
            }
    )
    @AssignUserId
    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> updateProfile(
            @Parameter(hidden = true) Long userId,
            @RequestBody UserProfileUpdateRequest request
    );
}
