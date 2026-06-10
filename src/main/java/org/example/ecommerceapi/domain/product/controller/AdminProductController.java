package org.example.ecommerceapi.domain.product.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.ecommerceapi.domain.product.dto.InventoryUpdateRequest;
import org.example.ecommerceapi.domain.product.dto.ProductCreateRequest;
import org.example.ecommerceapi.domain.product.dto.ProductResponse;
import org.example.ecommerceapi.domain.product.dto.ProductUpdateRequest;
import org.example.ecommerceapi.domain.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductResponse response = productService.createProduct(
                request.name(), request.price(), request.stock()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long productId
    ) {
        ProductResponse response = productService.getProductForAdmin(productId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<ProductResponse> response = productService.findProductsForAdmin(keyword, pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId, @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductResponse response = productService.updateProduct(
                productId, request.name(), request.price()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}/inventory")
    public ResponseEntity<ProductResponse> updateInventory(
            @PathVariable Long productId, @Valid @RequestBody InventoryUpdateRequest request
    ) {
        ProductResponse response = productService.updateInventory(
                productId, request.stock()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}/activate")
    public ResponseEntity<Void> activateProduct(@PathVariable Long productId) {
        productService.activateProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.noContent().build();
    }

}