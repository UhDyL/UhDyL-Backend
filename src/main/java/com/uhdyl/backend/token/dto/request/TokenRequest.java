package com.uhdyl.backend.token.dto.request;

public record TokenRequest (
    String accessToken,
    String refreshToken
){

}
