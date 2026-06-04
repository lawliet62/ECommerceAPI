package org.example.ecommerceapi.domain.order.repository;

import org.example.ecommerceapi.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}