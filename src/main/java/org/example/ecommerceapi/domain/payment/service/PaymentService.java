package org.example.ecommerceapi.domain.payment.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public PaymentResponse createPayment(Long orderId, Long userId) {
        Order order = getOrder(orderId, userId);
        validatePayableOrder(order);
        validatePendingPaymentNotExists(order);

        Payment payment = Payment.create(order);
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.from(savedPayment);
    }

    public PaymentResponse findPayment(Long paymentId, Long userId) {
        Payment payment = getPayment(paymentId, userId);

        return PaymentResponse.from(payment);
    }

    public Page<PaymentResponse> findPaymentsByOrder(Long orderId, Long userId, Pageable pageable) {
        Order order = getOrder(orderId, userId);

        return paymentRepository.findAllByOrder(order, pageable)
                .map(PaymentResponse::from);
    }

    public Page<PaymentResponse> findPayments(Long userId, Pageable pageable) {
        return paymentRepository.findAllByOrderUserId(userId, pageable)
                .map(PaymentResponse::from);
    }

    @Transactional
    public PaymentResponse completePayment(Long paymentId, Long userId) {
        Payment payment = getPayment(paymentId, userId);
        validatePendingPayment(payment);

        Order order = payment.getOrder();
        validatePayableOrder(order);

        deductStock(order);
        complete(payment);
        order.markAsPaid();

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse cancelPayment(Long paymentId, Long userId) {
        Payment payment = getPayment(paymentId, userId);
        validatePendingPayment(payment);
        validatePayableOrder(payment.getOrder());

        cancel(payment);

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse failPayment(Long paymentId, Long userId) {
        Payment payment = getPayment(paymentId, userId);
        validatePendingPayment(payment);
        validatePayableOrder(payment.getOrder());

        fail(payment);

        return PaymentResponse.from(payment);
    }

    private Order getOrder(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private Payment getPayment(Long paymentId, Long userId) {
        return paymentRepository.findByIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    private void validatePayableOrder(Order order) {
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(ErrorCode.ORDER_NOT_PAYABLE);
        }
    }

    private void validatePendingPayment(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_PROCESSABLE);
        }
    }

    private void validatePendingPaymentNotExists(Order order) {
        if (paymentRepository.existsByOrderAndStatus(order, PaymentStatus.PENDING)) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }
    }

    private void complete(Payment payment) {
        try {
            payment.complete();
        } catch (IllegalStateException exception) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_PROCESSABLE);
        }
    }

    private void cancel(Payment payment) {
        try {
            payment.cancel();
        } catch (IllegalStateException exception) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_CANCELABLE);
        }
    }

    private void fail(Payment payment) {
        try {
            payment.fail();
        } catch (IllegalStateException exception) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_PROCESSABLE);
        }
    }

    private void deductStock(Order order) {
        List<OrderItem> items = orderItemRepository.findAllByOrder(order);

        for (OrderItem item : items) {
            Product product = item.getProduct();

            try {
                product.decreaseStock(item.getQuantity());
            } catch (IllegalStateException exception) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }
        }
    }
}
