package org.example.ecommerceapi.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.example.ecommerceapi.domain.user.entity.AppUser;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    public static Order create(AppUser user, BigDecimal totalAmount) {
        return new Order(user, totalAmount);
    }

    public void markAsPaid() {
        if (this.status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only pending payment orders can be paid");
        }

        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (this.status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only pending payment orders can be cancelled");
        }

        this.status = OrderStatus.CANCELLED;
    }

    private Order(@NonNull AppUser user, BigDecimal totalAmount) {
        validateTotalAmount(totalAmount);

        this.user = user;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    private static void validateTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be positive");
        }
    }
}