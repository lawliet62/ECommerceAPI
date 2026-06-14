package org.example.ecommerceapi.domain.payment.dto;

import org.example.ecommerceapi.domain.payment.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        PaymentStatus status,
        BigDecimal amount
) {
}