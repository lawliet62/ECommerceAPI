package org.example.ecommerceapi.domain.order.repository;

import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrder(Order order);
}