package org.example.ecommerceapi.domain.payment.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.ecommerceapi.domain.payment.dto.PaymentResponse;
import org.example.ecommerceapi.domain.payment.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}/payments")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable @Positive Long orderId,
            Authentication authentication
    ) {
        PaymentResponse response = paymentService.createPayment(
                orderId, getUserId(authentication)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<PaymentResponse> findPayment(
            @PathVariable @Positive Long paymentId,
            Authentication authentication
    ) {
        PaymentResponse response = paymentService.findPayment(
                paymentId, getUserId(authentication)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{orderId}/payments")
    public ResponseEntity<Page<PaymentResponse>> findPaymentsByOrder(
            @PathVariable @Positive Long orderId,
            Authentication authentication,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<PaymentResponse> responses = paymentService.findPaymentsByOrder(
                orderId, getUserId(authentication), pageable
        );

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/payments")
    public ResponseEntity<Page<PaymentResponse>> findPayments(
            Authentication authentication,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<PaymentResponse> responses = paymentService.findPayments(
                getUserId(authentication), pageable
        );

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/payments/{paymentId}/complete")
    public ResponseEntity<PaymentResponse> completePayment(
            @PathVariable @Positive Long paymentId,
            Authentication authentication
    ) {
        PaymentResponse response = paymentService.completePayment(
                paymentId, getUserId(authentication)
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/payments/{paymentId}/fail")
    public ResponseEntity<PaymentResponse> failPayment(
            @PathVariable @Positive Long paymentId,
            Authentication authentication
    ) {
        PaymentResponse response = paymentService.failPayment(
                paymentId, getUserId(authentication)
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/payments/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @PathVariable @Positive Long paymentId,
            Authentication authentication
    ) {
        PaymentResponse response = paymentService.cancelPayment(
                paymentId, getUserId(authentication)
        );

        return ResponseEntity.ok(response);
    }

    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}