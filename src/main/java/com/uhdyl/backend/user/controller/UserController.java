package com.uhdyl.backend.user.controller;


import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.token.domain.Token;
import com.uhdyl.backend.token.dto.response.TokenResponse;
import com.uhdyl.backend.user.api.UserApi;
import com.uhdyl.backend.user.dto.request.LocationRequest;
import com.uhdyl.backend.user.dto.request.UserNicknameUpdateRequest;
import com.uhdyl.backend.user.dto.request.UserProfileUpdateRequest;
import com.uhdyl.backend.user.dto.response.UserProfileResponse;
import com.uhdyl.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @AssignUserId
    @DeleteMapping("/logout")
    @PreAuthorize(" isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> logout(Long userId){
        userService.logout(userId);
        return ResponseEntity.ok(createSuccessResponse());
    }

    @AssignUserId
    @PostMapping("/location")
    @PreAuthorize(" isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> saveLocation(@RequestBody LocationRequest request,
                                                           @Parameter(hidden = true) Long userId){
        userService.saveLocation(userId, request.getLocation_x(), request.getLocation_y());
        return ResponseEntity.ok(createSuccessResponse());
    }

    /**
     * 구매자가 처음으로 판매자로 전환 시 권한을 변경하고 토큰을 재발급하는 api
     */
    @AssignUserId
    @PostMapping("/complete-registration")
    @PreAuthorize(" isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<TokenResponse>> completeRegistration(
            Long userId
    ){
        Token token = userService.completeRegistration(userId);
        return ResponseEntity.ok(createSuccessResponse(TokenResponse.to(token)));
    }

    @AssignUserId
    @PostMapping("/nickname")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> updateNickname(
            Long userId,
            @RequestBody UserNicknameUpdateRequest request
    ){
        userService.updateNickname(userId, request);
        return ResponseEntity.ok(createSuccessResponse());
    }

    @AssignUserId
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<UserProfileResponse>> getProfile(Long userId){
        return ResponseEntity.ok(createSuccessResponse(userService.getProfile(userId)));
    }

    @AssignUserId
    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> updateProfile(
            Long userId,
            @RequestBody UserProfileUpdateRequest request
    ){
        userService.updateProfile(userId, request);
        return ResponseEntity.ok(createSuccessResponse());
    }
}
