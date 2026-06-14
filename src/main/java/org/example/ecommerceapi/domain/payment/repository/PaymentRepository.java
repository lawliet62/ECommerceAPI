package org.example.ecommerceapi.domain.payment.repository;

import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByOrderUserId(Long userId, Pageable pageable);

    Optional<Payment> findByIdAndOrderUserId(Long paymentId, Long userId);

    Optional<Payment> findByOrder(Order order);

    boolean existsByOrder(Order order);
}
