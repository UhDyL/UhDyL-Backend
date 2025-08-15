package com.uhdyl.backend.zzim.controller;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.zzim.api.ZzimApi;
import com.uhdyl.backend.zzim.dto.request.ZzimToggleRequest;
import com.uhdyl.backend.zzim.dto.response.ZzimResponse;
import com.uhdyl.backend.zzim.dto.response.ZzimToggleResponse;
import com.uhdyl.backend.zzim.service.ZzimService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;

@RestController
@RequiredArgsConstructor
public class ZzimController implements ZzimApi {
    private final ZzimService zzimService;

    @GetMapping("/zzim")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ZzimResponse>>> getZzims(
            Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(createSuccessResponse(zzimService.getZzims(userId, pageable)));
    }

    @PostMapping("/zzim")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ZzimToggleResponse>> toggleZzim(
            Long userId,
            @RequestBody ZzimToggleRequest request
    ){
        return ResponseEntity.ok(createSuccessResponse(zzimService.toggleZzim(userId, request)));
    }
}
