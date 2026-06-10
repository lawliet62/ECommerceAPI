package org.example.ecommerceapi.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean active;

    private Product(String name, BigDecimal price, int stock) {
        validateName(name);
        validatePrice(price);
        validateStock(stock);

        this.name = name;
        this.price = price;
        this.stock = stock;
        this.active = true;
    }

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

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
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
}