package org.example.ecommerceapi.domain.cart.repository;

import org.example.ecommerceapi.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}