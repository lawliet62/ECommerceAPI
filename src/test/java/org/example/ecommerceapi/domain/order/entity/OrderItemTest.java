package org.example.ecommerceapi.domain.order.entity;

import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemTest {

    @Test
    void create_withValidValues_createsOrderItemWithProductSnapshot() {
        Order order = createOrder();
        Product product = createProduct();

        OrderItem orderItem = OrderItem.create(order, product, 2);

        assertEquals(order, orderItem.getOrder());
        assertEquals(product, orderItem.getProduct());
        assertEquals("Product 1", orderItem.getProductNameSnapshot());
        assertEquals(BigDecimal.valueOf(10), orderItem.getPriceSnapshot());
        assertEquals(2, orderItem.getQuantity());
    }

    @Test
    void create_copiesProductSnapshotAtCreationTime() {
        Order order = createOrder();
        Product product = createProduct();

        OrderItem orderItem = OrderItem.create(order, product, 2);

        product.updateInfo("Product 2", BigDecimal.valueOf(15));

        assertEquals("Product 1", orderItem.getProductNameSnapshot());
        assertEquals(BigDecimal.valueOf(10), orderItem.getPriceSnapshot());
    }

    @Test
    void create_withInvalidQuantity_throwsException() {
        Order order = createOrder();
        Product product = createProduct();

        assertThrows(IllegalArgumentException.class,
                () -> OrderItem.create(order, product, 0));

        assertThrows(IllegalArgumentException.class,
                () -> OrderItem.create(order, product, 100));
    }

    @Test
    void getSubtotal_returnsPriceSnapshotMultipliedByQuantity() {
        OrderItem orderItem = createOrderItem(2);

        assertEquals(BigDecimal.valueOf(20), orderItem.getSubtotal());
    }

    private Order createOrder() {
        AppUser user = AppUser.createUser("user@example.com", "encodedPassword");
        return Order.create(user, BigDecimal.valueOf(20));
    }

    private Product createProduct() {
        return Product.create("Product 1", BigDecimal.valueOf(10), 10);
    }

    private OrderItem createOrderItem(int quantity) {
        return OrderItem.create(createOrder(), createProduct(), quantity);
    }
}
