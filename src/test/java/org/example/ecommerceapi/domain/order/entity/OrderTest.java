package org.example.ecommerceapi.domain.order.entity;

import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void create_withValidValues_createsPendingPaymentOrder() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Order order = Order.create(user, BigDecimal.valueOf(100.0));

        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(user, order.getUser());
        assertEquals(BigDecimal.valueOf(100.0), order.getTotalAmount());
    }

    @Test
    void create_withNullTotalAmount_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");

        assertThrows(
                IllegalArgumentException.class,
                () -> Order.create(user, null)
        );
    }

    @Test
    void create_withZeroOrNegativeTotalAmount_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");

        assertThrows(
                IllegalArgumentException.class,
                () -> Order.create(user, BigDecimal.valueOf(0.0))
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Order.create(user, BigDecimal.valueOf(-100.0))
        );
    }

    @Test
    void markAsPaid_whenPendingPayment_changesStatusToPaid() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Order order = Order.create(user, BigDecimal.valueOf(100.0));
        order.markAsPaid();

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void markAsPaid_whenNotPendingPayment_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Order order = Order.create(user, BigDecimal.valueOf(100.0));
        order.markAsPaid();

        assertThrows(
                IllegalStateException.class,
                order::markAsPaid
        );
    }

    @Test
    void cancel_whenPendingPayment_changesStatusToCancelled() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Order order = Order.create(user, BigDecimal.valueOf(100.0));
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_whenNotPendingPayment_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Order order = Order.create(user, BigDecimal.valueOf(100.0));
        order.markAsPaid();

        assertThrows(
                IllegalStateException.class,
                order::cancel
        );

    }
}
