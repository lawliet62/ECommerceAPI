package org.example.ecommerceapi.domain.product.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void create_withValidValues_createsActiveProduct() {
        Product product = createProduct();

        assertEquals("Keyboard", product.getName());
        assertEquals(BigDecimal.valueOf(30000), product.getPrice());
        assertEquals(10, product.getStock());
        assertTrue(product.isActive());
    }

    @Test
    void create_withBlankName_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(" ", BigDecimal.valueOf(30000), 10)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(null, BigDecimal.valueOf(30000), 10)
        );
    }

    @Test
    void create_withNameLongerThan100_throwsException() {
        String tooLongName = "a".repeat(101);

        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(tooLongName, BigDecimal.valueOf(30000), 10)
        );
    }

    @Test
    void create_withNullPrice_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create("Keyboard", null, 10)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-30000"})
    void create_withNonPositivePrice_throwsException(String price) {
        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create("Keyboard", new BigDecimal(price), 10)
        );
    }

    @Test
    void create_withNegativeStock_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create("Keyboard", BigDecimal.valueOf(30000), -10)
        );
    }

    @Test
    void updateInfo_withValidValues_updatesNameAndPrice() {
        Product product = createProduct();
        product.updateInfo("Razer keyboard", BigDecimal.valueOf(100000));

        assertEquals("Razer keyboard", product.getName());
        assertEquals(BigDecimal.valueOf(100000), product.getPrice());
    }

    @Test
    void updateInfo_withBlankName_throwsException() {
        Product product = createProduct();

        assertThrows(
                IllegalArgumentException.class,
                () -> product.updateInfo(" ", BigDecimal.valueOf(100000))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void updateInfo_withNonPositivePrice_throwsException(String price) {
        Product product = createProduct();

        assertThrows(
                IllegalArgumentException.class,
                () -> product.updateInfo("Mouse", new BigDecimal(price))
        );
    }

    @Test
    void updateStock_withValidStock_updatesStock() {
        Product product = createProduct();
        product.updateStock(5);

        assertEquals(5, product.getStock());
    }

    @Test
    void updateStock_withNegativeStock_throwsException() {
        Product product = createProduct();

        assertThrows(
                IllegalArgumentException.class,
                () -> product.updateStock(-1)
        );
    }

    @Test
    void decreaseStock_withEnoughStock_decreasesStock() {
        Product product = createProduct();
        product.decreaseStock(5);

        assertEquals(5, product.getStock());
    }

    @Test
    void decreaseStock_withInsufficientStock_throwsException() {
        Product product = createProduct();
        assertThrows(
                IllegalStateException.class,
                () -> product.decreaseStock(15)
        );
    }

    @Test
    void decreaseStock_withZeroOrNegativeQuantity_throwsException() {
        Product product = createProduct();
        assertThrows(
                IllegalArgumentException.class,
                () -> product.decreaseStock(0)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> product.decreaseStock(-10)
        );
    }

    @Test
    void activate_setsActiveTrue() {
        Product product = createProduct();
        product.activate();

        assertTrue(product.isActive());
    }

    @Test
    void deactivate_setsActiveFalse() {
        Product product = createProduct();
        product.deactivate();

        assertFalse(product.isActive());
    }

    private Product createProduct() {
        return Product.create("Keyboard", BigDecimal.valueOf(30000), 10);
    }
}
