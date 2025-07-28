package com.uhdyl.backend.global.jwt.exception;


import com.uhdyl.backend.global.exception.ExceptionType;
import lombok.Getter;

@Getter
public class JwtTokenExpiredException extends JwtAuthenticationException {

    public JwtTokenExpiredException(Throwable cause) {
        super(cause, ExceptionType.JWT_EXPIRED);
    }

}
