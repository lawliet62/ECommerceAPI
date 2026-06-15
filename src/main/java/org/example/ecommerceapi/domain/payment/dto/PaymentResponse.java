package org.example.ecommerceapi.domain.payment.dto;

import org.example.ecommerceapi.domain.payment.entity.Payment;
import org.example.ecommerceapi.domain.payment.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        PaymentStatus status,
        BigDecimal amount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getStatus(),
                payment.getAmount()
        );
    }
}