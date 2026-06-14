package org.example.ecommerceapi.domain.order.repository;

import org.example.ecommerceapi.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}
