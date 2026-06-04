package org.example.ecommerceapi.domain.product.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        boolean active
) {
}