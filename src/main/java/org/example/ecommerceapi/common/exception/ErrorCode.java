package org.example.ecommerceapi.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "Invalid email or password"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart not found"),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart item not found"),
    EMPTY_CART(HttpStatus.NO_CONTENT, "Empty cart"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found"),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "Insufficient stock"),
    PRODUCT_NOT_ORDERABLE(HttpStatus.CONFLICT, "Product not orderable"),
    ORDER_NOT_PAYABLE(HttpStatus.CONFLICT, "Order not payable"),
    PAYMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "Payment already exists"),
    ORDER_NOT_CANCELABLE(HttpStatus.CONFLICT, "Order not cancelable");

    private final HttpStatus status;
    private final String message;
}
