package org.example.ecommerceapi.domain.order.dto;

import org.example.ecommerceapi.domain.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long orderId,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items
) {
}