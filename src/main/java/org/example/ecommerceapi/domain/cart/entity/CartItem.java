package org.example.ecommerceapi.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.ecommerceapi.domain.product.entity.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"cart_id", "product_id"})
        }
)
public class CartItem {

    private static final int MAX_QUANTITY = 99;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public CartItem(Cart cart, Product product, int quantity) {
        validateCart(cart);
        validateProduct(product);
        validateQuantity(quantity);

        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public static CartItem create(Cart cart, Product product, int quantity) {
        return new CartItem(cart, product, quantity);
    }

    private static void validateCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart is required");
        }
    }

    private static void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
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