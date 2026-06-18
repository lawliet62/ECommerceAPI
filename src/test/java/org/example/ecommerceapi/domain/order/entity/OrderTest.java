package org.example.ecommerceapi.domain.order.entity;

import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void create_withValidValues_createsPendingPaymentOrder() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(100));

        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(user, order.getUser());
        assertEquals(BigDecimal.valueOf(100), order.getTotalAmount());
    }

    @Test
    void create_withNullTotalAmount_throwsException() {
        AppUser user = createUser();

        assertThrows(
                IllegalArgumentException.class,
                () -> Order.create(user, null)
        );
    }

    @Test
    void create_withZeroOrNegativeTotalAmount_throwsException() {
        AppUser user = createUser();

        assertThrows(
                IllegalArgumentException.class,
                () -> Order.create(user, BigDecimal.ZERO)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Order.create(user, BigDecimal.valueOf(-100))
        );
    }

    @Test
    void markAsPaid_whenPendingPayment_changesStatusToPaid() {
        Order order = createPendingPaymentOrder();
        order.markAsPaid();

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void markAsPaid_whenNotPendingPayment_throwsException() {
        assertThrows(
                IllegalStateException.class,
                createPaidOrder()::markAsPaid
        );

        assertThrows(
                IllegalStateException.class,
                createCancelledOrder()::markAsPaid
        );
    }

    @Test
    void cancel_whenPendingPayment_changesStatusToCancelled() {
        Order order = createPendingPaymentOrder();
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_whenNotPendingPayment_throwsException() {
        assertThrows(
                IllegalStateException.class,
                createPaidOrder()::cancel
        );

        assertThrows(
                IllegalStateException.class,
                createCancelledOrder()::cancel
        );
    }

    private AppUser createUser() {
        return AppUser.createUser("user@example.com", "encodedPassword");
    }

    private Order createPendingPaymentOrder() {
        return Order.create(createUser(), BigDecimal.valueOf(100));
    }

    private Order createPaidOrder() {
        Order order = createPendingPaymentOrder();
        order.markAsPaid();
        return order;
    }

    private Order createCancelledOrder() {
        Order order = createPendingPaymentOrder();
        order.cancel();
        return order;
    }
}
