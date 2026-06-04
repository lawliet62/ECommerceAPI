package org.example.ecommerceapi.domain.order.repository;

import org.example.ecommerceapi.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}