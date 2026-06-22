package org.example.ecommerceapi.domain.payment.service;

import org.example.ecommerceapi.common.exception.BusinessException;
import org.example.ecommerceapi.common.exception.ErrorCode;
import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.order.entity.OrderItem;
import org.example.ecommerceapi.domain.order.entity.OrderStatus;
import org.example.ecommerceapi.domain.order.repository.OrderItemRepository;
import org.example.ecommerceapi.domain.order.repository.OrderRepository;
import org.example.ecommerceapi.domain.payment.dto.PaymentResponse;
import org.example.ecommerceapi.domain.payment.entity.Payment;
import org.example.ecommerceapi.domain.payment.entity.PaymentStatus;
import org.example.ecommerceapi.domain.payment.repository.PaymentRepository;
import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_withPayableOrderAndNoPendingPayment_createsPendingPayment() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));

        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderAndStatus(order, PaymentStatus.PENDING))
                .thenReturn(false);
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.createPayment(1L, 1L);

        verify(paymentRepository).save(any(Payment.class));

        assertEquals(PaymentStatus.PENDING, response.status());
        assertEquals(BigDecimal.valueOf(10000), response.amount());
    }

    @Test
    void createPayment_whenOrderNotFound_throwsOrderNotFound() {
        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> paymentService.createPayment(1L, 1L)
                );

        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createPayment_withNonPayableOrder_throwsOrderNotPayable() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        order.markAsPaid();

        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(order));

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> paymentService.createPayment(1L, 1L)
                );

        assertEquals(ErrorCode.ORDER_NOT_PAYABLE, exception.getErrorCode());
    }

    @Test
    void createPayment_whenPendingPaymentExists_throwsPaymentAlreadyExists() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));

        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderAndStatus(order, PaymentStatus.PENDING))
                .thenReturn(true);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> paymentService.createPayment(1L, 1L)
                );

        assertEquals(ErrorCode.PAYMENT_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void findPayment_withOwnedPayment_returnsResponse() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment = Payment.create(order);

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.findPayment(1L, 1L);

        assertEquals(PaymentStatus.PENDING, response.status());
        assertEquals(BigDecimal.valueOf(10000), response.amount());
    }

    @Test
    void findPayment_whenPaymentNotFoundForUser_throwsPaymentNotFound() {
        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.findPayment(1L, 1L)
        );

        assertEquals(ErrorCode.PAYMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void findPaymentsByOrder_returnsOrderPayments() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment1 = Payment.create(order);
        Payment payment2 = Payment.create(order);
        payment2.cancel();

        Pageable pageable = createPageable();
        Page<Payment> paymentsPage = createPaymentPage(payment1, payment2);

        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(order));
        when(paymentRepository.findAllByOrder(order, pageable))
                .thenReturn(paymentsPage);

        Page<PaymentResponse> responses = paymentService.findPaymentsByOrder(1L, 1L, pageable);

        assertEquals(2, responses.getTotalElements());
        assertEquals(PaymentStatus.PENDING, responses.getContent().get(0).status());
        assertEquals(PaymentStatus.CANCELLED, responses.getContent().get(1).status());
        assertEquals(BigDecimal.valueOf(10000), responses.getContent().get(0).amount());
        assertEquals(BigDecimal.valueOf(10000), responses.getContent().get(1).amount());
    }

    @Test
    void findPayments_returnsUserPayments() {
        AppUser user = createUser();
        Order order1 = Order.create(user, BigDecimal.valueOf(10000));
        Order order2 = Order.create(user, BigDecimal.valueOf(20000));

        Payment payment1 = Payment.create(order1);
        Payment payment2 = Payment.create(order2);

        Pageable pageable = createPageable();
        Page<Payment> paymentsPage = createPaymentPage(payment1, payment2);

        when(paymentRepository.findAllByOrderUserId(1L, pageable))
            .thenReturn(paymentsPage);

        Page<PaymentResponse> responses = paymentService.findPayments(1L, pageable);

        assertEquals(2, responses.getTotalElements());
        assertEquals(2, responses.getContent().size());
        assertEquals(PaymentStatus.PENDING, responses.getContent().get(0).status());
        assertEquals(PaymentStatus.PENDING, responses.getContent().get(1).status());
        assertEquals(BigDecimal.valueOf(10000), responses.getContent().get(0).amount());
        assertEquals(BigDecimal.valueOf(20000), responses.getContent().get(1).amount());
    }

    @Test
    void completePayment_whenPending_decreasesStockAndMarksPaymentSuccessAndOrderPaid() {
        AppUser user = createUser();
        Product product = createProduct();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment = Payment.create(order);
        OrderItem orderItem =
                OrderItem.create(order, product, 1);

        List<OrderItem> orderItems = List.of(orderItem);

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));
        when(orderItemRepository.findAllByOrder(order))
                .thenReturn(orderItems);

        PaymentResponse response = paymentService.completePayment(1L, 1L);

        assertEquals(PaymentStatus.SUCCESS, response.status());
        assertEquals(BigDecimal.valueOf(10000), response.amount());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(9, product.getStock());
    }

    @Test
    void completePayment_whenPaymentNotPending_throwsPaymentNotProcessable() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment = Payment.create(order);
        payment.cancel();

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.completePayment(1L, 1L)
        );

        assertEquals(ErrorCode.PAYMENT_NOT_PROCESSABLE, exception.getErrorCode());
    }

    @Test
    void completePayment_whenOrderNotPayable_throwsOrderNotPayable() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        order.markAsPaid();
        Payment payment = Payment.create(order);

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.completePayment(1L, 1L)
        );

        assertEquals(ErrorCode.ORDER_NOT_PAYABLE, exception.getErrorCode());
    }

    @Test
    void completePayment_whenStockInsufficient_throwsInsufficientStock() {
        AppUser user = createUser();
        Product product = createProduct();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment = Payment.create(order);
        OrderItem orderItem =
                OrderItem.create(order, product, 15);

        List<OrderItem> orderItems = List.of(orderItem);

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));

        when(orderItemRepository.findAllByOrder(order))
                .thenReturn(orderItems);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.completePayment(1L, 1L)
        );

        assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    }

    @Test
    void cancelPayment_whenPending_marksPaymentCancelledAndKeepsOrderPendingPayment() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment = Payment.create(order);

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.cancelPayment(1L, 1L);

        assertEquals(PaymentStatus.CANCELLED, response.status());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
    }

    @Test
    void cancelPayment_whenNotPending_throwsPaymentNotProcessable() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment = Payment.create(order);
        payment.fail();

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.cancelPayment(1L, 1L)
        );

        assertEquals(ErrorCode.PAYMENT_NOT_PROCESSABLE, exception.getErrorCode());
    }

    @Test
    void failPayment_whenPending_marksPaymentFailedAndKeepsOrderPendingPayment() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment = Payment.create(order);

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.failPayment(1L, 1L);

        assertEquals(PaymentStatus.FAILED, response.status());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
    }

    @Test
    void failPayment_whenNotPending_throwsPaymentNotProcessable() {
        AppUser user = createUser();
        Order order = Order.create(user, BigDecimal.valueOf(10000));
        Payment payment = Payment.create(order);
        payment.cancel();

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L))
                .thenReturn(Optional.of(payment));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.failPayment(1L, 1L)
        );

        assertEquals(ErrorCode.PAYMENT_NOT_PROCESSABLE, exception.getErrorCode());
    }

    private AppUser createUser() {
        return AppUser.createUser("user@example.com", "encodedPassword");
    }

    private Product createProduct() {
        return Product.create("Keyboard", BigDecimal.valueOf(10000), 10);
    }

    private Pageable createPageable() {
        return PageRequest.of(0, 10);
    }

    private Page<Payment> createPaymentPage(Payment... payments) {
        return new PageImpl<>(List.of(payments), createPageable(), payments.length);
    }

}
