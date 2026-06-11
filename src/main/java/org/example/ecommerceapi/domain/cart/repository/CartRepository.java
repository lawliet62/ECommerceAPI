package org.example.ecommerceapi.domain.cart.repository;

import org.example.ecommerceapi.domain.cart.entity.Cart;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    Optional<Cart> findByUser(AppUser user);

}