package com.uhdyl.backend.user.controller;


import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.user.api.UserApi;
import com.uhdyl.backend.user.dto.request.LocationRequest;
import com.uhdyl.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @AssignUserId
    @DeleteMapping("/logout")
    @PreAuthorize(" isAuthenticated() and hasAuthority('USER')")
    public ResponseEntity<ResponseBody<Void>> logout(Long userId){
        userService.logout(userId);
        return ResponseEntity.ok(createSuccessResponse());
    }

    @AssignUserId
    @PostMapping("/location")
    @PreAuthorize(" isAuthenticated() and hasAuthority('USER')")
    public ResponseEntity<ResponseBody<Void>> saveLocation(@RequestBody LocationRequest request,
                                                           @Parameter(hidden = true) Long userId){
        userService.saveLocation(userId, request.getLocation_x(), request.getLocation_y());
        return ResponseEntity.ok(createSuccessResponse());
    }
}
