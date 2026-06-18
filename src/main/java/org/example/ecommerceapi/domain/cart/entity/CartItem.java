package org.example.ecommerceapi.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.example.ecommerceapi.domain.product.entity.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "cart_item",
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

    public static CartItem create(Cart cart, Product product, int quantity) {
        return new CartItem(cart, product, quantity);
    }

    public CartItem increaseQuantity(int amount) {
        validateIncreaseAmount(amount);
        validateQuantity(this.quantity + amount);

        this.quantity += amount;
        return this;
    }

    public void updateQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    private CartItem(@NonNull Cart cart, @NonNull Product product, int quantity) {
        validateQuantity(quantity);

        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }

        if (quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Quantity must be 99 or less");
        }
    }

    private static void validateIncreaseAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Increase amount must be positive");
        }
    }

}
