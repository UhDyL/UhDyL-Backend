package com.uhdyl.backend.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openapitools.jackson.nullable.JsonNullable;

public record UserProfileUpdateRequest (
        @Schema(description = "프로필 이미지 url", example = "http://어쩌구저쩌구")
        JsonNullable<String> profileImageUrl,

        @Schema(description = "유저 닉네임", example = "치킨먹는고양이")
        JsonNullable<String> nickname,

        @Schema(description = "사용자의 현재 모드", example = "구매자|판매자")
        String mode
){

}
