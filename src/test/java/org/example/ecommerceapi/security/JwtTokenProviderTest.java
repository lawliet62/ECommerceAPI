package org.example.ecommerceapi.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderTest {

    private static final String SECRET =
            "01234567890123456789012345678901";
    private static final long EXPIRATION = 60_000L;
    private static final Long USER_ID = 1L;

    private final JwtTokenProvider jwtTokenProvider =
            new JwtTokenProvider(new JwtProperties(SECRET, EXPIRATION));

    @Test
    void generateToken_withUserId_returnsToken() {
        String token = jwtTokenProvider.generateToken(USER_ID);

        assertFalse(token.isBlank());
        assertEquals(USER_ID, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void getUserIdFromToken_withValidToken_returnsUserId() {
        String token = jwtTokenProvider.generateToken(USER_ID);

        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        assertEquals(USER_ID, userId);
    }

    @Test
    void getUserIdFromToken_withInvalidToken_throwsException() {
        assertThrows(
                RuntimeException.class,
                () -> jwtTokenProvider.getUserIdFromToken("invalid-token")
        );
    }
}
