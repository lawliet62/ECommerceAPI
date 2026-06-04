package org.example.ecommerceapi.domain.order.dto;

import org.example.ecommerceapi.domain.order.entity.OrderStatus;

public record OrderResponse(
        Long orderId,
        OrderStatus status
) {
}