package org.example.ecommerceapi.domain.payment.repository;

import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.payment.entity.Payment;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByOrderUser(AppUser user, Pageable pageable);

    Optional<Payment> findByIdAndOrderUser(Long paymentId, AppUser user);

    Optional<Payment> findByOrder(Order order);

    boolean existsByOrder(Order order);
}