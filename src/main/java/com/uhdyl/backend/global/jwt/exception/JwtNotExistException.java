package com.uhdyl.backend.global.jwt.exception;


import com.uhdyl.backend.global.exception.ExceptionType;

public class JwtNotExistException extends JwtAuthenticationException {
    public JwtNotExistException() {
        super(ExceptionType.JWT_NOT_EXIST);
    }
}
