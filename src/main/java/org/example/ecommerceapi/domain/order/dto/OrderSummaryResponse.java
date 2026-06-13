package org.example.ecommerceapi.domain.order.dto;

import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.order.entity.OrderStatus;

import java.math.BigDecimal;

public record OrderSummaryResponse(
        Long orderId,
        OrderStatus status,
        BigDecimal totalAmount
) {
    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount()
        );
    }
}
