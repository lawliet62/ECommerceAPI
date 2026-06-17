package org.example.ecommerceapi.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean active;

    public static Product create(String name, BigDecimal price, int stock) {
        return new Product(name, price, stock);
    }

    public void updateInfo(String name, BigDecimal price) {
        validateName(name);
        validatePrice(price);

        this.name = name;
        this.price = price;
    }

    public void updateStock(int stock) {
        validateStock(stock);

        this.stock = stock;
    }

    public void decreaseStock(int quantity) {
        validateDecreaseQuantity(quantity);

        if (this.stock < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }

        this.stock -= quantity;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private Product(String name, BigDecimal price, int stock) {
        validateName(name);
        validatePrice(price);
        validateStock(stock);

        this.name = name;
        this.price = price;
        this.stock = stock;
        this.active = true;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("Product name must be 100 characters or less");
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
    }

    private static void validateStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock must be zero or positive");
        }
    }

    private static void validateDecreaseQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Decrease quantity must be positive");
        }
    }
}
