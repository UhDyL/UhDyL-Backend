package com.uhdyl.backend.user.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

public record UserProfileUpdateRequest (
        JsonNullable<String> profileImageUrl,
        JsonNullable<String> nickname
){

}
