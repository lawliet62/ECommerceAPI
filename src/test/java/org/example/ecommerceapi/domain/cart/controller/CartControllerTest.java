package org.example.ecommerceapi.domain.cart.controller;

import org.example.ecommerceapi.domain.cart.dto.CartItemResponse;
import org.example.ecommerceapi.domain.cart.dto.CartResponse;
import org.example.ecommerceapi.domain.cart.service.CartService;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.example.ecommerceapi.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void addItem_withValidRequest_returnsCreatedItem() throws Exception {
        CartItemResponse response = createCartItemResponse(2);

        when(cartService.addItem(USER_ID, 1L, 2))
                .thenReturn(response);

        mockMvc.perform(post("/cart/items")
                        .principal(createAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":1,"quantity":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.subtotal").value(20000));

        verify(cartService).addItem(USER_ID, 1L, 2);
    }

    @Test
    void addItem_withInvalidRequest_returnsValidationError() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .principal(createAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":0,"quantity":2}
                                """))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void getCart_returnsCart() throws Exception {
        CartResponse response = new CartResponse(1L, List.of(createCartItemResponse(2)));

        when(cartService.getCart(USER_ID))
                .thenReturn(response);

        mockMvc.perform(get("/cart")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.items[0].cartItemId").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(cartService).getCart(USER_ID);
    }

    @Test
    void updateItem_withValidRequest_returnsUpdatedItem() throws Exception {
        CartItemResponse response = createCartItemResponse(3);

        when(cartService.updateItem(USER_ID, 1L, 3))
                .thenReturn(response);

        mockMvc.perform(patch("/cart/items/1")
                        .principal(createAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quantity":3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.subtotal").value(30000));

        verify(cartService).updateItem(USER_ID, 1L, 3);
    }

    @Test
    void updateItem_withInvalidCartItemId_returnsValidationError() throws Exception {
        mockMvc.perform(patch("/cart/items/0")
                        .principal(createAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quantity":3}
                                """))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).updateItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void removeItem_withValidId_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/cart/items/1")
                        .principal(createAuthentication()))
                .andExpect(status().isNoContent());

        verify(cartService).removeItem(USER_ID, 1L);
    }

    private TestingAuthenticationToken createAuthentication() {
        return new TestingAuthenticationToken(USER_ID, "test-credentials");
    }

    private CartItemResponse createCartItemResponse(int quantity) {
        return new CartItemResponse(
                1L,
                1L,
                "Keyboard",
                BigDecimal.valueOf(10000),
                quantity,
                BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(quantity))
        );
    }
}
