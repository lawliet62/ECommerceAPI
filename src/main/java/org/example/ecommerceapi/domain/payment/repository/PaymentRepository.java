package org.example.ecommerceapi.domain.payment.repository;

import org.example.ecommerceapi.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}