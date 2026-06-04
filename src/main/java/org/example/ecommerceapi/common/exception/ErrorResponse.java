package org.example.ecommerceapi.common.exception;

public record ErrorResponse(
        String code,
        String message
) {
}