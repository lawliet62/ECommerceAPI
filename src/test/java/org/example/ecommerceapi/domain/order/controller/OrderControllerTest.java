package org.example.ecommerceapi.domain.order.controller;

import org.example.ecommerceapi.domain.order.dto.OrderItemResponse;
import org.example.ecommerceapi.domain.order.dto.OrderResponse;
import org.example.ecommerceapi.domain.order.dto.OrderSummaryResponse;
import org.example.ecommerceapi.domain.order.entity.OrderStatus;
import org.example.ecommerceapi.domain.order.service.OrderService;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.example.ecommerceapi.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void createOrder_returnsCreatedOrder() throws Exception {
        OrderResponse response = createOrderResponse();

        when(orderService.createOrder(USER_ID))
                .thenReturn(response);

        mockMvc.perform(post("/orders")
                        .principal(createAuthentication()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.totalAmount").value(20000))
                .andExpect(jsonPath("$.items[0].orderItemId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Keyboard"));

        verify(orderService).createOrder(USER_ID);
    }

    @Test
    void findOrder_withValidId_returnsOrder() throws Exception {
        OrderResponse response = createOrderResponse();

        when(orderService.findOrder(1L, USER_ID))
                .thenReturn(response);

        mockMvc.perform(get("/orders/1")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.totalAmount").value(20000));

        verify(orderService).findOrder(1L, USER_ID);
    }

    @Test
    void findOrder_withInvalidId_returnsValidationError() throws Exception {
        mockMvc.perform(get("/orders/0")
                        .principal(createAuthentication()))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).findOrder(anyLong(), anyLong());
    }

    @Test
    void findOrders_withDefaultPaging_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<OrderSummaryResponse> response = createOrderSummaryPage(pageable);

        when(orderService.findOrders(USER_ID, pageable))
                .thenReturn(response);

        mockMvc.perform(get("/orders")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.content[0].totalAmount").value(20000))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(orderService).findOrders(USER_ID, pageable);
    }

    @Test
    void findOrders_withInvalidPaging_returnsValidationError() throws Exception {
        mockMvc.perform(get("/orders")
                        .principal(createAuthentication())
                        .param("page", "0")
                        .param("limit", "20"))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).findOrders(anyLong(), any());
    }

    @Test
    void cancelOrder_withValidId_returnsNoContent() throws Exception {
        mockMvc.perform(patch("/orders/1/cancel")
                        .principal(createAuthentication()))
                .andExpect(status().isNoContent());

        verify(orderService).cancelOrder(1L, USER_ID);
    }

    private TestingAuthenticationToken createAuthentication() {
        return new TestingAuthenticationToken(USER_ID, "test-credentials");
    }

    private OrderResponse createOrderResponse() {
        return new OrderResponse(
                1L,
                OrderStatus.PENDING_PAYMENT,
                BigDecimal.valueOf(20000),
                List.of(new OrderItemResponse(
                        1L,
                        "Keyboard",
                        BigDecimal.valueOf(10000),
                        2,
                        BigDecimal.valueOf(20000)
                ))
        );
    }

    private Page<OrderSummaryResponse> createOrderSummaryPage(Pageable pageable) {
        return new PageImpl<>(
                List.of(new OrderSummaryResponse(
                        1L,
                        OrderStatus.PENDING_PAYMENT,
                        BigDecimal.valueOf(20000)
                )),
                pageable,
                1
        );
    }
}
