package org.example.ecommerceapi.integration;

import com.jayway.jsonpath.JsonPath;
import org.example.ecommerceapi.domain.cart.repository.CartItemRepository;
import org.example.ecommerceapi.domain.cart.repository.CartRepository;
import org.example.ecommerceapi.domain.order.entity.OrderStatus;
import org.example.ecommerceapi.domain.order.repository.OrderItemRepository;
import org.example.ecommerceapi.domain.order.repository.OrderRepository;
import org.example.ecommerceapi.domain.payment.entity.PaymentStatus;
import org.example.ecommerceapi.domain.payment.repository.PaymentRepository;
import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.product.repository.ProductRepository;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PurchaseFlowIntegrationTest {

    private static final String EMAIL = "buyer@example.com";
    private static final String PASSWORD = "password123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void cleanDatabase() {
        paymentRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void purchaseFlow_withValidRequests_completesPaymentAndDeductsStock() throws Exception {
        String accessToken = registerAndLogin();
        Product product = productRepository.saveAndFlush(
                Product.create("Keyboard", BigDecimal.valueOf(10000), 5)
        );

        addCartItem(accessToken, product.getId(), 2);
        long orderId = createOrder(accessToken);
        long paymentId = createPayment(accessToken, orderId);

        mockMvc.perform(patch("/payments/{paymentId}/complete", paymentId)
                        .header("Authorization", bearer(accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId))
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(20000));

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock())
                .isEqualTo(3);
        assertThat(orderRepository.findById(orderId).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.PAID);
        assertThat(paymentRepository.findById(paymentId).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.SUCCESS);
        assertThat(cartItemRepository.findAll()).isEmpty();
    }

    @Test
    void completePayment_withInsufficientStock_keepsPendingStatesAndStock() throws Exception {
        String accessToken = registerAndLogin();
        Product product = productRepository.saveAndFlush(
                Product.create("Keyboard", BigDecimal.valueOf(10000), 2)
        );

        addCartItem(accessToken, product.getId(), 2);
        long orderId = createOrder(accessToken);
        product.updateStock(1);
        productRepository.saveAndFlush(product);
        long paymentId = createPayment(accessToken, orderId);

        mockMvc.perform(patch("/payments/{paymentId}/complete", paymentId)
                        .header("Authorization", bearer(accessToken)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"))
                .andExpect(jsonPath("$.message").value("Insufficient stock"));

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock())
                .isEqualTo(1);
        assertThat(orderRepository.findById(orderId).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(paymentRepository.findById(paymentId).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void cart_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    private String registerAndLogin() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(EMAIL, PASSWORD)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    private void addCartItem(String accessToken, Long productId, int quantity) throws Exception {
        mockMvc.perform(post("/cart/items")
                        .header("Authorization", bearer(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": %d,
                                  "quantity": %d
                                }
                                """.formatted(productId, quantity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.quantity").value(quantity));
    }

    private long createOrder(String accessToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/orders")
                        .header("Authorization", bearer(accessToken)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
                .andReturn();

        return ((Number) JsonPath.read(
                result.getResponse().getContentAsString(), "$.orderId"
        )).longValue();
    }

    private long createPayment(String accessToken, long orderId) throws Exception {
        MvcResult result = mockMvc.perform(post("/orders/{orderId}/payments", orderId)
                        .header("Authorization", bearer(accessToken)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        return ((Number) JsonPath.read(
                result.getResponse().getContentAsString(), "$.paymentId"
        )).longValue();
    }

    private String bearer(String accessToken) {
        return "Bearer " + accessToken;
    }
}
