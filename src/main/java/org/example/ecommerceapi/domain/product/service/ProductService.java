package org.example.ecommerceapi.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.example.ecommerceapi.common.exception.BusinessException;
import org.example.ecommerceapi.common.exception.ErrorCode;
import org.example.ecommerceapi.domain.product.dto.ProductResponse;
import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(String name, BigDecimal price, int stock) {
        Product product = Product.create(name, price, stock);
        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    public ProductResponse getActiveProduct(Long productId) {
        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponse.from(product);
    }

    public Page<ProductResponse> findActiveProducts(String keyword, Pageable pageable) {
        Page<Product> products;

        if (keyword == null || keyword.isBlank()) {
            products = productRepository.findAllByActiveTrue(pageable);
        } else {
            products = productRepository.findAllByActiveTrueAndNameContainingIgnoreCase(keyword, pageable);
        }

        return products.map(ProductResponse::from);
    }

    public ProductResponse getProductForAdmin(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponse.from(product);
    }

    public Page<ProductResponse> findProductsForAdmin(String keyword, Pageable pageable) {
        Page<Product> products;

        if (keyword == null || keyword.isBlank()) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
        }

        return products.map(ProductResponse::from);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, String name, BigDecimal price) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.updateInfo(name, price);

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateInventory(Long productId, int stock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.updateStock(stock);

        return ProductResponse.from(product);
    }

    @Transactional
    public void activateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.activate();
    }

    @Transactional
    public void deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.deactivate();
    }

}