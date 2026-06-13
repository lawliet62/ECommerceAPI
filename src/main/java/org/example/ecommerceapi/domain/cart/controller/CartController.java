package org.example.ecommerceapi.domain.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.ecommerceapi.domain.cart.dto.AddItemRequest;
import org.example.ecommerceapi.domain.cart.dto.CartItemResponse;
import org.example.ecommerceapi.domain.cart.dto.CartResponse;
import org.example.ecommerceapi.domain.cart.dto.UpdateItemRequest;
import org.example.ecommerceapi.domain.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody AddItemRequest request
    ) {
        CartItemResponse response = cartService.addItem(
                getUserId(authentication),
                request.productId(),
                request.quantity()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        CartResponse response = cartService.getCart(
                getUserId(authentication)
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateItem(
            Authentication authentication,
            @PathVariable @Positive Long cartItemId,
            @Valid @RequestBody UpdateItemRequest request
    ) {
        CartItemResponse response = cartService.updateItem(
                getUserId(authentication),
                cartItemId,
                request.quantity()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            Authentication authentication,
            @PathVariable @Positive Long cartItemId
    ) {
        cartService.removeItem(
                getUserId(authentication),
                cartItemId
        );

        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }

}