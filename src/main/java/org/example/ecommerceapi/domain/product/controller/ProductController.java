package org.example.ecommerceapi.domain.product.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.ecommerceapi.domain.product.dto.ProductResponse;
import org.example.ecommerceapi.domain.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getActiveProduct(
            @PathVariable @Positive Long productId
    ) {
        ProductResponse response = productService.getActiveProduct(productId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findActiveProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<ProductResponse> response = productService.findActiveProducts(keyword, pageable);

        return ResponseEntity.ok(response);
    }
}