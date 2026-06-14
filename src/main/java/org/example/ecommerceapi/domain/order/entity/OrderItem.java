package org.example.ecommerceapi.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.example.ecommerceapi.domain.product.entity.Product;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderItem {

    private static final int MAX_QUANTITY = 99;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 100)
    private String productNameSnapshot;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal priceSnapshot;

    @Column(nullable = false)
    private int quantity;

    public static OrderItem create(Order order, Product product, int quantity) {
        return new OrderItem(
                order,
                product,
                product.getName(),
                product.getPrice(),
                quantity
        );
    }

    public BigDecimal getSubtotal() {
        return priceSnapshot.multiply(BigDecimal.valueOf(quantity));
    }

    private OrderItem(
            @NonNull Order order, @NonNull Product product,
            String productNameSnapshot, BigDecimal priceSnapshot, int quantity
    ) {
        validateProductNameSnapshot(productNameSnapshot);
        validatePriceSnapshot(priceSnapshot);
        validateQuantity(quantity);

        this.order = order;
        this.product = product;
        this.productNameSnapshot = productNameSnapshot;
        this.priceSnapshot = priceSnapshot;
        this.quantity = quantity;
    }

    private static void validateProductNameSnapshot(String productNameSnapshot) {
        if (productNameSnapshot == null || productNameSnapshot.isBlank()) {
            throw new IllegalArgumentException("Product Name Snapshot is required");
        }

        if (productNameSnapshot.length() > 100) {
            throw new IllegalArgumentException("Product Name Snapshot must be 100 characters or less");
        }
    }

    private static void validatePriceSnapshot(BigDecimal priceSnapshot) {
        if (priceSnapshot == null || priceSnapshot.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }

        if (quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Quantity must be 99 or less");
        }
    }

}