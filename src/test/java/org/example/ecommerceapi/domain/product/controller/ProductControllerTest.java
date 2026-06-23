package org.example.ecommerceapi.domain.product.controller;

import org.example.ecommerceapi.domain.product.dto.ProductResponse;
import org.example.ecommerceapi.domain.product.service.ProductService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void getActiveProduct_withValidId_returnsProduct() throws Exception {
        ProductResponse response =
                new ProductResponse(1L, "Keyboard", BigDecimal.valueOf(10000), 10, true);

        when(productService.getActiveProduct(1L))
                .thenReturn(response);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.active").value(true));

        verify(productService).getActiveProduct(1L);
    }

    @Test
    void getActiveProduct_withInvalidId_returnsValidationError() throws Exception {
        mockMvc.perform(get("/products/0"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).getActiveProduct(anyLong());
    }

    @Test
    void findActiveProducts_withDefaultPaging_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponse> response = createProductResponsePage(pageable);

        when(productService.findActiveProducts(null, pageable))
                .thenReturn(response);

        mockMvc.perform(get("/products")
                        .param("page", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"))
                .andExpect(jsonPath("$.content[0].price").value(10000))
                .andExpect(jsonPath("$.content[0].stock").value(10))
                .andExpect(jsonPath("$.content[0].active").value(true))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(productService).findActiveProducts(null, pageable);
    }

    @Test
    void findActiveProducts_withKeyword_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponse> response = createProductResponsePage(pageable);

        when(productService.findActiveProducts("Keyboard", pageable))
                .thenReturn(response);

        mockMvc.perform(get("/products")
                        .param("keyword", "Keyboard")
                        .param("page", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"))
                .andExpect(jsonPath("$.content[0].price").value(10000))
                .andExpect(jsonPath("$.content[0].stock").value(10))
                .andExpect(jsonPath("$.content[0].active").value(true))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(productService).findActiveProducts("Keyboard", pageable);
    }

    @Test
    void findActiveProducts_withInvalidPaging_returnsValidationError() throws Exception {
        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).findActiveProducts(anyString(), any());
    }

    private Page<ProductResponse> createProductResponsePage(Pageable pageable) {
        return new PageImpl<>(
                List.of(createProductResponse()),
                pageable,
                1
        );
    }

    private ProductResponse createProductResponse() {
        return new ProductResponse(
                1L, "Keyboard", BigDecimal.valueOf(10000), 10, true
        );
    }
}
