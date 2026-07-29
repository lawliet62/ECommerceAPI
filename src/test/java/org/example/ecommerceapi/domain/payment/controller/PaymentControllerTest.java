package org.example.ecommerceapi.domain.payment.controller;

import org.example.ecommerceapi.domain.payment.dto.PaymentResponse;
import org.example.ecommerceapi.domain.payment.entity.PaymentStatus;
import org.example.ecommerceapi.domain.payment.service.PaymentService;
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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void createPayment_withValidOrderId_returnsCreatedPayment() throws Exception {
        PaymentResponse response = createPaymentResponse(PaymentStatus.PENDING);

        when(paymentService.createPayment(1L, USER_ID))
                .thenReturn(response);

        mockMvc.perform(post("/orders/1/payments")
                        .principal(createAuthentication()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(20000));

        verify(paymentService).createPayment(1L, USER_ID);
    }

    @Test
    void findPayment_withValidId_returnsPayment() throws Exception {
        PaymentResponse response = createPaymentResponse(PaymentStatus.PENDING);

        when(paymentService.findPayment(1L, USER_ID))
                .thenReturn(response);

        mockMvc.perform(get("/payments/1")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(20000));

        verify(paymentService).findPayment(1L, USER_ID);
    }

    @Test
    void findPaymentsByOrder_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<PaymentResponse> response = createPaymentResponsePage(pageable);

        when(paymentService.findPaymentsByOrder(1L, USER_ID, pageable))
                .thenReturn(response);

        mockMvc.perform(get("/orders/1/payments")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].paymentId").value(1))
                .andExpect(jsonPath("$.content[0].orderId").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.content[0].amount").value(20000))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(paymentService).findPaymentsByOrder(1L, USER_ID, pageable);
    }

    @Test
    void findPayments_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<PaymentResponse> response = createPaymentResponsePage(pageable);

        when(paymentService.findPayments(USER_ID, pageable))
                .thenReturn(response);

        mockMvc.perform(get("/payments")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].paymentId").value(1))
                .andExpect(jsonPath("$.content[0].orderId").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.content[0].amount").value(20000))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(paymentService).findPayments(USER_ID, pageable);
    }

    @Test
    void completePayment_returnsPayment() throws Exception {
        PaymentResponse response = createPaymentResponse(PaymentStatus.SUCCESS);

        when(paymentService.completePayment(1L, USER_ID))
                .thenReturn(response);

        mockMvc.perform(patch("/payments/1/complete")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService).completePayment(1L, USER_ID);
    }

    @Test
    void failPayment_returnsPayment() throws Exception {
        PaymentResponse response = createPaymentResponse(PaymentStatus.FAILED);

        when(paymentService.failPayment(1L, USER_ID))
                .thenReturn(response);

        mockMvc.perform(patch("/payments/1/fail")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.status").value("FAILED"));

        verify(paymentService).failPayment(1L, USER_ID);
    }

    @Test
    void cancelPayment_returnsPayment() throws Exception {
        PaymentResponse response = createPaymentResponse(PaymentStatus.CANCELLED);

        when(paymentService.cancelPayment(1L, USER_ID))
                .thenReturn(response);

        mockMvc.perform(patch("/payments/1/cancel")
                        .principal(createAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(paymentService).cancelPayment(1L, USER_ID);
    }

    @Test
    void requestWithInvalidPathVariable_returnsValidationError() throws Exception {
        mockMvc.perform(get("/payments/0")
                        .principal(createAuthentication()))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).findPayment(anyLong(), anyLong());
    }

    @Test
    void requestWithInvalidPaging_returnsValidationError() throws Exception {
        mockMvc.perform(get("/payments")
                        .principal(createAuthentication())
                        .param("page", "1")
                        .param("limit", "101"))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).findPayments(anyLong(), any());
    }

    private TestingAuthenticationToken createAuthentication() {
        return new TestingAuthenticationToken(USER_ID, "test-credentials");
    }

    private Page<PaymentResponse> createPaymentResponsePage(Pageable pageable) {
        return new PageImpl<>(
                List.of(createPaymentResponse(PaymentStatus.PENDING)),
                pageable,
                1
        );
    }

    private PaymentResponse createPaymentResponse(PaymentStatus status) {
        return new PaymentResponse(
                1L,
                1L,
                status,
                BigDecimal.valueOf(20000)
        );
    }
}
