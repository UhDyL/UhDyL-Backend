package com.uhdyl.backend.global.jwt.exception;


import com.uhdyl.backend.global.exception.ExceptionType;

public class JwtAccessDeniedException extends JwtAuthenticationException {
    public JwtAccessDeniedException() {
        super(ExceptionType.ACCESS_DENIED);
    }
}
