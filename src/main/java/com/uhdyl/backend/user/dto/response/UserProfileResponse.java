package com.uhdyl.backend.user.dto.response;

import com.uhdyl.backend.user.domain.User;

public record UserProfileResponse(
        String profileImageUrl,
        String nickname,
        String role
) {
    public static UserProfileResponse to(User user){
        return new UserProfileResponse(
                user.getPicture(),
                user.getNickname(),
                user.getRole().name()
        );
    }
}
