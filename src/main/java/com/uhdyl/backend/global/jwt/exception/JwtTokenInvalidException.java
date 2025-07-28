package com.uhdyl.backend.global.jwt.exception;


import com.uhdyl.backend.global.exception.ExceptionType;

public class JwtTokenInvalidException extends JwtAuthenticationException {
    public JwtTokenInvalidException() {
        super(ExceptionType.JWT_INVALID);
    }

    public JwtTokenInvalidException(Throwable cause) {
        super(cause, ExceptionType.JWT_INVALID);
    }
}
