package com.uhdyl.backend.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum UserRole {

    USER("ROLE_USER"),
    FARMER("ROLE_FARMER");

    private final String key;
}