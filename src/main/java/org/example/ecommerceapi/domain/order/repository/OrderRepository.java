package org.example.ecommerceapi.domain.order.repository;

import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(AppUser user, Pageable pageable);

    Optional<Order> findByIdAndUser(Long orderId, AppUser user);
}
