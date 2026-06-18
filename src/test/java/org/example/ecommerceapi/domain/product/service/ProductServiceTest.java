package org.example.ecommerceapi.domain.product.service;

import org.example.ecommerceapi.common.exception.BusinessException;
import org.example.ecommerceapi.common.exception.ErrorCode;
import org.example.ecommerceapi.domain.product.dto.ProductResponse;
import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_savesProductAndReturnsResponse() {
        stubSaveReturnsArgument();

        ProductResponse response =
                productService.createProduct("keyboard", BigDecimal.valueOf(10000), 10);

        verify(productRepository).save(any(Product.class));

        assertEquals("keyboard", response.name());
        assertEquals(BigDecimal.valueOf(10000), response.price());
        assertEquals(10, response.stock());
        assertTrue(response.active());
    }

    @Test
    void getActiveProduct_withActiveProduct_returnsResponse() {
        Product product = createProduct();

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.getActiveProduct(1L);

        assertEquals("keyboard", response.name());
        assertEquals(BigDecimal.valueOf(10000), response.price());
        assertEquals(10, response.stock());
        assertTrue(response.active());
    }

    @Test
    void getActiveProduct_whenProductNotFound_throwsProductNotFound() {
        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertProductNotFoundThrown(() -> productService.getActiveProduct(1L));
    }

    @Test
    void findActiveProducts_withoutKeyword_returnsActiveProducts() {
        Product product1 = createProduct();
        Product product2 = createProduct("Mouse");
        Pageable pageable = createPageable();

        when(productRepository.findAllByActiveTrue(pageable))
                .thenReturn(createProductPage(product1, product2));

        Page<ProductResponse> responsePage =
                productService.findActiveProducts(null, pageable);

        verify(productRepository).findAllByActiveTrue(pageable);
        verify(productRepository, never())
                .findAllByActiveTrueAndNameContainingIgnoreCase(anyString(), eq(pageable));

        assertEquals(2, responsePage.getTotalElements());
        assertEquals(2, responsePage.getContent().size());

        assertProductResponse(product1, responsePage.getContent().get(0));
        assertProductResponse(product2, responsePage.getContent().get(1));
    }

    @Test
    void findActiveProducts_withKeyword_returnsMatchingActiveProducts() {
        Product product2 = createProduct("Mouse");
        Pageable pageable = createPageable();

        when(productRepository.findAllByActiveTrueAndNameContainingIgnoreCase("Mouse", pageable))
                .thenReturn(createProductPage(product2));

        Page<ProductResponse> responsePage =
                productService.findActiveProducts("Mouse", pageable);

        verify(productRepository)
                .findAllByActiveTrueAndNameContainingIgnoreCase("Mouse", pageable);
        verify(productRepository, never()).findAllByActiveTrue(pageable);

        assertEquals(1, responsePage.getTotalElements());
        assertEquals(1, responsePage.getContent().size());

        assertProductResponse(product2, responsePage.getContent().get(0));
    }

    @Test
    void getProductForAdmin_withAnyProduct_returnsResponse() {
        Product product = createInactiveProduct();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductForAdmin(1L);

        assertEquals("keyboard", response.name());
        assertEquals(BigDecimal.valueOf(10000), response.price());
        assertEquals(10, response.stock());
        assertFalse(response.active());
    }

    @Test
    void getProductForAdmin_whenProductNotFound_throwsProductNotFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertProductNotFoundThrown(() -> productService.getProductForAdmin(1L));
    }

    @Test
    void findProductsForAdmin_withoutKeyword_returnsAllProducts() {
        Product product1 = createProduct();
        Product product2 = createProduct("Mouse");
        Pageable pageable = createPageable();

        when(productRepository.findAll(pageable))
                .thenReturn(createProductPage(product1, product2));

        Page<ProductResponse> responsePage =
                productService.findProductsForAdmin(null, pageable);

        verify(productRepository).findAll(pageable);
        verify(productRepository, never())
                .findAllByNameContainingIgnoreCase(anyString(), eq(pageable));

        assertEquals(2, responsePage.getTotalElements());
        assertEquals(2, responsePage.getContent().size());

        assertProductResponse(product1, responsePage.getContent().get(0));
        assertProductResponse(product2, responsePage.getContent().get(1));

    }

    @Test
    void findProductsForAdmin_withKeyword_returnsMatchingProducts() {
        Product product2 = createProduct("Mouse");
        Pageable pageable = createPageable();

        when(productRepository.findAllByNameContainingIgnoreCase("Mouse", pageable))
                .thenReturn(createProductPage(product2));

        Page<ProductResponse> responsePage =
                productService.findProductsForAdmin("Mouse", pageable);

        verify(productRepository).findAllByNameContainingIgnoreCase("Mouse", pageable);
        verify(productRepository, never()).findAll(pageable);

        assertEquals(1, responsePage.getTotalElements());
        assertEquals(1, responsePage.getContent().size());

        assertProductResponse(product2, responsePage.getContent().get(0));
    }

    @Test
    void updateProduct_updatesProductInfo() {
        Product product = createProduct();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productService.updateProduct(1L, "Mouse", BigDecimal.valueOf(20000));

        assertEquals("Mouse", response.name());
        assertEquals(BigDecimal.valueOf(20000), response.price());
    }

    @Test
    void updateProduct_whenProductNotFound_throwsProductNotFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertProductNotFoundThrown(
                () -> productService.updateProduct(1L, "Mouse", BigDecimal.valueOf(20000))
        );
    }

    @Test
    void updateInventory_updatesStock() {
        Product product = createProduct();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productService.updateInventory(1L, 5);

        assertEquals(5, response.stock());
    }

    @Test
    void updateInventory_whenProductNotFound_throwsProductNotFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertProductNotFoundThrown(() -> productService.updateInventory(1L, 5));
    }

    @Test
    void activateProduct_activatesProduct() {
        Product product = createProduct();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.activateProduct(1L);

        assertTrue(product.isActive());
    }

    @Test
    void activateProduct_whenProductNotFound_throwsProductNotFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertProductNotFoundThrown(() -> productService.activateProduct(1L));
    }

    @Test
    void deactivateProduct_deactivatesProduct() {
        Product product = createProduct();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deactivateProduct(1L);

        assertFalse(product.isActive());
    }

    @Test
    void deactivateProduct_whenProductNotFound_throwsProductNotFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertProductNotFoundThrown(() -> productService.deactivateProduct(1L));
    }

    private Product createProduct() {
        return Product.create("keyboard", BigDecimal.valueOf(10000), 10);
    }

    private Product createProduct(String name) {
        return Product.create(name, BigDecimal.valueOf(10000), 10);
    }

    private Product createInactiveProduct() {
        Product product = createProduct();
        product.deactivate();
        return product;
    }

    private Pageable createPageable() {
        return PageRequest.of(0, 10);
    }

    private Page<Product> createProductPage(Product... products) {
        return new PageImpl<>(List.of(products), createPageable(), products.length);
    }

    private void stubSaveReturnsArgument() {
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void assertProductResponse(Product product, ProductResponse response) {
        assertEquals(product.getName(), response.name());
        assertEquals(product.getPrice(), response.price());
        assertEquals(product.getStock(), response.stock());
        assertEquals(product.isActive(), response.active());
    }

    private void assertProductNotFoundThrown(Executable executable) {
        BusinessException exception = assertThrows(BusinessException.class, executable);

        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }
}
