package org.example.ecommerceapi.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "Invalid email or password");

    private final HttpStatus status;
    private final String message;
}
