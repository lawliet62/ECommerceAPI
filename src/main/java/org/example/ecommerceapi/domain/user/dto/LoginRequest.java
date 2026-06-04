package org.example.ecommerceapi.domain.user.dto;

public record LoginRequest(
        String email,
        String password
) {
}