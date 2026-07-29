package org.example.ecommerceapi.domain.user.controller;

import org.example.ecommerceapi.domain.user.dto.LoginResponse;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.example.ecommerceapi.domain.user.service.AuthService;
import org.example.ecommerceapi.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void register_withValidRequest_returnsCreated() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"password"}
                                """))
                .andExpect(status().isCreated());

        verify(authService).register("user@example.com", "password");
    }

    @Test
    void register_withInvalidRequest_returnsValidationError() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"invalid-email","password":"password"}
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(anyString(), anyString());
    }

    @Test
    void login_withValidRequest_returnsToken() throws Exception {
        when(authService.login("user@example.com", "password"))
                .thenReturn(new LoginResponse("access-token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));

        verify(authService).login("user@example.com", "password");
    }

    @Test
    void login_withInvalidRequest_returnsValidationError() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"short"}
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(anyString(), anyString());
    }
}
