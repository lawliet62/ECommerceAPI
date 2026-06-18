package org.example.ecommerceapi.domain.cart.entity;

import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void create_withUser_createsCart() {
        AppUser user = createUser();
        Cart cart = Cart.create(user);

        assertEquals(user, cart.getUser());
    }

    private AppUser createUser() {
        return AppUser.createUser("user@example.com", "encodedPassword");
    }
}
