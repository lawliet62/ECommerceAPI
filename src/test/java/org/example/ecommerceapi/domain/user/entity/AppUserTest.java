package org.example.ecommerceapi.domain.user.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    @Test
    void createUser_withValidValues_createsUserWithUserRole() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");

        assertEquals("user@example.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void createUser_withBlankEmail_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AppUser.createUser("", "encodedPassword")
        );
    }

    @Test
    void createUser_withBlankPassword_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AppUser.createUser("user@example.com", "")
        );
    }
}
