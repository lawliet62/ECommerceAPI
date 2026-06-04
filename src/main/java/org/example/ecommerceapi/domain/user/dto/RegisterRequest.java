package org.example.ecommerceapi.domain.user.dto;

public record RegisterRequest(
        String email,
        String password
) {
}