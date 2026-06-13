package org.example.ecommerceapi.domain.order.dto;

import org.example.ecommerceapi.domain.order.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long orderItemId,
        String productName,
        BigDecimal price,
        int quantity,
        BigDecimal subtotal
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getProductNameSnapshot(),
                orderItem.getPriceSnapshot(),
                orderItem.getQuantity(),
                orderItem.getSubtotal()
        );
    }
}
