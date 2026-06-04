package org.example.ecommerceapi.domain.product.dto;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        BigDecimal price,
        int stock
) {
}