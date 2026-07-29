package org.example.ecommerceapi.config;

import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.example.ecommerceapi.security.JwtAuthenticationFilter;
import org.example.ecommerceapi.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigTest.TestController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        SecurityConfigTest.TestController.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void adminEndpoint_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_withUserRole_returnsForbidden() throws Exception {
        mockMvc.perform(get("/admin/products")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_withAdminRole_returnsOk() throws Exception {
        mockMvc.perform(get("/admin/products")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void publicProductEndpoint_withoutAuthentication_returnsOk() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    void loginEndpoint_withoutAuthentication_returnsOk() throws Exception {
        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void registerEndpoint_withoutAuthentication_returnsOk() throws Exception {
        mockMvc.perform(post("/auth/register"))
                .andExpect(status().isOk());
    }

    @Test
    void cartEndpoint_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ordersEndpoint_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());
    }

    @RestController
    public static class TestController {

        @GetMapping("/admin/products")
        String adminProducts() {
            return "admin";
        }

        @GetMapping("/products")
        String products() {
            return "products";
        }

        @PostMapping("/auth/login")
        String login() {
            return "login";
        }

        @PostMapping("/auth/register")
        String register() {
            return "register";
        }

        @GetMapping("/cart")
        String cart() {
            return "cart";
        }

        @GetMapping("/orders")
        String orders() {
            return "orders";
        }
    }
}
