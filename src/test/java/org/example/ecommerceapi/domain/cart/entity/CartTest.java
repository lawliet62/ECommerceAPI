package org.example.ecommerceapi.domain.cart.entity;

import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void create_withUser_createsCart() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        Cart cart = Cart.create(user);

        assertEquals(user, cart.getUser());
    }

    @Test
    void create_withNullUser_throwsException() {
        assertThrows(
                NullPointerException.class,
                () -> Cart.create(null)
        );
    }
}
