package org.example.ecommerceapi.security;

import jakarta.servlet.FilterChain;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final Long USER_ID = 1L;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_withoutAuthorizationHeader_continuesFilterChain() throws Exception {
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtTokenProvider, appUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider, never()).getUserIdFromToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withValidToken_setsAuthentication() throws Exception {
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtTokenProvider, appUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");

        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtTokenProvider.getUserIdFromToken("valid-token"))
                .thenReturn(USER_ID);
        when(appUserRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        filter.doFilter(request, response, filterChain);

        UsernamePasswordAuthenticationToken authentication =
                assertInstanceOf(
                        UsernamePasswordAuthenticationToken.class,
                        SecurityContextHolder.getContext().getAuthentication()
                );

        assertEquals(USER_ID, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority())));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withInvalidToken_clearsSecurityContext() throws Exception {
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtTokenProvider, appUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_ID, null)
        );
        request.addHeader("Authorization", "Bearer invalid-token");
        when(jwtTokenProvider.getUserIdFromToken("invalid-token"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(appUserRepository, never()).findById(USER_ID);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withTokenForMissingUser_clearsSecurityContext() throws Exception {
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtTokenProvider, appUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_ID, null)
        );
        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtTokenProvider.getUserIdFromToken("valid-token"))
                .thenReturn(USER_ID);
        when(appUserRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
