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
    public static OrderResponse of(
            Long id, OrderStatus status, BigDecimal totalAmount,
            List<OrderItemResponse> items
    ) {
        return new OrderResponse(id, status, totalAmount, items);
    }
}