package org.example.ecommerceapi.domain.cart.repository;

import org.example.ecommerceapi.domain.cart.entity.Cart;
import org.example.ecommerceapi.domain.cart.entity.CartItem;
import org.example.ecommerceapi.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Optional<CartItem> findByIdAndCart(Long cartItemId, Cart cart);
}