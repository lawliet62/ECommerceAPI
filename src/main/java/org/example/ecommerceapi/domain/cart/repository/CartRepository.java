package org.example.ecommerceapi.domain.cart.repository;

import org.example.ecommerceapi.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}