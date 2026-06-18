package org.example.ecommerceapi.domain.cart.entity;

import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void create_withValidValues_createsCartItem() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);

        CartItem cartItem = CartItem.create(cart, product, 10);

        assertEquals(cart, cartItem.getCart());
        assertEquals(product, cartItem.getProduct());
        assertEquals(10, cartItem.getQuantity());
    }

    @Test
    void create_withZeroOrNegativeQuantity_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> CartItem.create(cart, product, 0)
        );
    }

    @Test
    void create_withQuantityGreaterThan99_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> CartItem.create(cart, product, 100)
        );
    }

    @Test
    void increaseQuantity_withValidAmount_increasesQuantity() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);
        CartItem cartItem = CartItem.create(cart, product, 10);

        cartItem.increaseQuantity(10);

        assertEquals(20, cartItem.getQuantity());
    }

    @Test
    void increaseQuantity_withZeroOrNegativeAmount_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);
        CartItem cartItem = CartItem.create(cart, product, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartItem.increaseQuantity(0)
        );
    }

    @Test
    void increaseQuantity_whenResultGreaterThan99_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);
        CartItem cartItem = CartItem.create(cart, product, 90);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartItem.increaseQuantity(10)
        );
    }

    @Test
    void updateQuantity_withValidQuantity_updatesQuantity() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);
        CartItem cartItem = CartItem.create(cart, product, 10);

        cartItem.updateQuantity(20);

        assertEquals(20, cartItem.getQuantity());
    }

    @Test
    void updateQuantity_withInvalidQuantity_throwsException() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);
        Product product = Product.create("Keyboard", BigDecimal.valueOf(30000), 10);
        CartItem cartItem = CartItem.create(cart, product, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartItem.updateQuantity(0)
        );
    }
}
