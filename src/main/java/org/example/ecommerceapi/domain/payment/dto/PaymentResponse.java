package org.example.ecommerceapi.domain.payment.dto;

import org.example.ecommerceapi.domain.payment.entity.PaymentStatus;

public record PaymentResponse(
        Long paymentId,
        PaymentStatus status
) {
}