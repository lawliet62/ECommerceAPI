package org.example.ecommerceapi.domain.cart.entity;

import org.example.ecommerceapi.domain.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void create_withValidValues_createsCartItem() {
        Cart cart = new Cart();
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);
        CartItem cartItem = CartItem.create(cart, product, 10);
        assertNotNull(cartItem);
        assertEquals(cart, cartItem.getCart());
        assertEquals(product, cartItem.getProduct());
        assertEquals(10, cartItem.getQuantity());
    }

    @Test
    void create_withNullCart_throwsException() {
    }

    @Test
    void create_withNullProduct_throwsException() {
    }

    @Test
    void create_withZeroOrNegativeQuantity_throwsException() {
    }

    @Test
    void create_withQuantityGreaterThan99_throwsException() {
    }

    @Test
    void increaseQuantity_withValidAmount_increasesQuantity() {
    }

    @Test
    void increaseQuantity_withZeroOrNegativeAmount_throwsException() {
    }

    @Test
    void increaseQuantity_whenResultGreaterThan99_throwsException() {
    }

    @Test
    void updateQuantity_withValidQuantity_updatesQuantity() {
    }

    @Test
    void updateQuantity_withInvalidQuantity_throwsException() {
    }
}
