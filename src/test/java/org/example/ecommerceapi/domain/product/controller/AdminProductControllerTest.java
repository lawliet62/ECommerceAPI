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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void createProduct_withValidRequest_returnsCreatedProduct() throws Exception {
        ProductResponse response = createProductResponse();

        when(productService.createProduct("Keyboard", BigDecimal.valueOf(10000), 10))
                .thenReturn(response);

        mockMvc.perform(post("/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Keyboard","price":10000,"stock":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.active").value(true));

        verify(productService).createProduct("Keyboard", BigDecimal.valueOf(10000), 10);
    }

    @Test
    void createProduct_withInvalidRequest_returnsValidationError() throws Exception {
        mockMvc.perform(post("/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","price":10000,"stock":10}
                                """))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(anyString(), any(BigDecimal.class), anyInt());
    }

    @Test
    void getProduct_withValidId_returnsProduct() throws Exception {
        ProductResponse response = createProductResponse();

        when(productService.getProductForAdmin(1L))
                .thenReturn(response);

        mockMvc.perform(get("/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.active").value(true));

        verify(productService).getProductForAdmin(1L);
    }

    @Test
    void getProduct_withInvalidId_returnsValidationError() throws Exception {
        mockMvc.perform(get("/admin/products/0"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).getProductForAdmin(anyLong());
    }

    @Test
    void findProducts_withDefaultPaging_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProductResponse> response = createProductResponsePage(pageable);

        when(productService.findProductsForAdmin(null, pageable))
                .thenReturn(response);

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"))
                .andExpect(jsonPath("$.content[0].price").value(10000))
                .andExpect(jsonPath("$.content[0].stock").value(10))
                .andExpect(jsonPath("$.content[0].active").value(true))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(productService).findProductsForAdmin(null, pageable);
    }

    @Test
    void findProducts_withInvalidPaging_returnsValidationError() throws Exception {
        mockMvc.perform(get("/admin/products")
                        .param("page", "0")
                        .param("limit", "20"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).findProductsForAdmin(any(), any());
    }

    @Test
    void updateProduct_withValidRequest_returnsUpdatedProduct() throws Exception {
        ProductResponse response =
                new ProductResponse(1L, "Mouse", BigDecimal.valueOf(20000), 10, true);

        when(productService.updateProduct(1L, "Mouse", BigDecimal.valueOf(20000)))
                .thenReturn(response);

        mockMvc.perform(put("/admin/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Mouse","price":20000}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.price").value(20000))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.active").value(true));

        verify(productService).updateProduct(1L, "Mouse", BigDecimal.valueOf(20000));
    }

    @Test
    void updateProduct_withInvalidRequest_returnsValidationError() throws Exception {
        mockMvc.perform(put("/admin/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Mouse","price":0}
                                """))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateProduct(anyLong(), anyString(), any(BigDecimal.class));
    }

    @Test
    void updateInventory_withValidRequest_returnsUpdatedProduct() throws Exception {
        ProductResponse response =
                new ProductResponse(1L, "Keyboard", BigDecimal.valueOf(10000), 5, true);

        when(productService.updateInventory(1L, 5))
                .thenReturn(response);

        mockMvc.perform(patch("/admin/products/1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stock":5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.stock").value(5))
                .andExpect(jsonPath("$.active").value(true));

        verify(productService).updateInventory(1L, 5);
    }

    @Test
    void updateInventory_withInvalidRequest_returnsValidationError() throws Exception {
        mockMvc.perform(patch("/admin/products/1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stock":-1}
                                """))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateInventory(anyLong(), anyInt());
    }

    @Test
    void activateProduct_returnsNoContent() throws Exception {
        mockMvc.perform(patch("/admin/products/1/activate"))
                .andExpect(status().isNoContent());

        verify(productService).activateProduct(1L);
    }

    @Test
    void deactivateProduct_returnsNoContent() throws Exception {
        mockMvc.perform(patch("/admin/products/1/deactivate"))
                .andExpect(status().isNoContent());

        verify(productService).deactivateProduct(1L);
    }

    private Page<ProductResponse> createProductResponsePage(Pageable pageable) {
        return new PageImpl<>(List.of(createProductResponse()), pageable, 1);
    }

    private ProductResponse createProductResponse() {
        return new ProductResponse(
                1L, "Keyboard", BigDecimal.valueOf(10000), 10, true
        );
    }
}
