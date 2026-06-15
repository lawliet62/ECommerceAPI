package org.example.ecommerceapi.domain.order.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.ecommerceapi.domain.order.dto.OrderResponse;
import org.example.ecommerceapi.domain.order.dto.OrderSummaryResponse;
import org.example.ecommerceapi.domain.order.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            Authentication authentication
    ) {
        OrderResponse response = orderService.createOrder(
                getUserId(authentication)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> findOrder(
            Authentication authentication,
            @PathVariable @Positive Long orderId
    ) {
        OrderResponse response = orderService.findOrder(
                orderId,
                getUserId(authentication)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponse>> findOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<OrderSummaryResponse> response = orderService.findOrders(
                getUserId(authentication), pageable
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            Authentication authentication,
            @PathVariable @Positive Long orderId
    ) {
        orderService.cancelOrder(orderId, getUserId(authentication));

        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }

}
