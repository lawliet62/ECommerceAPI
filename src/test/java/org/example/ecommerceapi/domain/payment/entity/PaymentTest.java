package org.example.ecommerceapi.domain.payment.entity;

import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void create_withValidOrder_createsPendingPayment() {
        Order order = createOrder();
        Payment payment = Payment.create(order);

        assertEquals(order, payment.getOrder());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(BigDecimal.TEN, payment.getAmount());
    }

    @Test
    void complete_whenPending_changesStatusToSuccess() {
        Payment payment = createPendingPayment();

        payment.complete();

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }

    @Test
    void complete_whenNotPending_throwsException() {
        assertThrows(IllegalStateException.class, createSuccessPayment()::complete);
        assertThrows(IllegalStateException.class, createCancelledPayment()::complete);
        assertThrows(IllegalStateException.class, createFailedPayment()::complete);
    }

    @Test
    void cancel_whenPending_changesStatusToCancelled() {
        Payment payment = createPendingPayment();

        payment.cancel();

        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
    }

    @Test
    void cancel_whenNotPending_throwsException() {
        assertThrows(IllegalStateException.class, createSuccessPayment()::cancel);
        assertThrows(IllegalStateException.class, createCancelledPayment()::cancel);
        assertThrows(IllegalStateException.class, createFailedPayment()::cancel);
    }

    @Test
    void fail_whenPending_changesStatusToFailed() {
        Payment payment = createPendingPayment();

        payment.fail();

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    void fail_whenNotPending_throwsException() {
        assertThrows(IllegalStateException.class, createSuccessPayment()::fail);
        assertThrows(IllegalStateException.class, createCancelledPayment()::fail);
        assertThrows(IllegalStateException.class, createFailedPayment()::fail);
    }

    private Order createOrder() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        return Order.create(user, BigDecimal.TEN);
    }

    private Payment createPendingPayment() {
        return Payment.create(createOrder());
    }

    private Payment createSuccessPayment() {
        Payment payment = createPendingPayment();
        payment.complete();
        return payment;
    }

    private Payment createCancelledPayment() {
        Payment payment = createPendingPayment();
        payment.cancel();
        return payment;
    }

    private Payment createFailedPayment() {
        Payment payment = createPendingPayment();
        payment.fail();
        return payment;
    }
}
