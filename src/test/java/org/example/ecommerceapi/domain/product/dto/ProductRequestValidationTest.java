package org.example.ecommerceapi.domain.product.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void productCreateRequest_withValidValues_passesValidation() {
        ProductCreateRequest request =
                new ProductCreateRequest("Valid Name", BigDecimal.valueOf(10000), 10);

        Set<ConstraintViolation<ProductCreateRequest>> violations = validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void productCreateRequest_withBlankName_failsValidation() {
        ProductCreateRequest request =
                new ProductCreateRequest(" ", BigDecimal.valueOf(10000), 10);

        Set<ConstraintViolation<ProductCreateRequest>> violations = validate(request);

        assertTrue(hasViolationForProperty(violations, "name"));
    }

    @Test
    void productCreateRequest_withNameLongerThan100_failsValidation() {
        ProductCreateRequest request =
                new ProductCreateRequest("a".repeat(101), BigDecimal.valueOf(10000), 10);

        Set<ConstraintViolation<ProductCreateRequest>> violations = validate(request);

        assertTrue(hasViolationForProperty(violations, "name"));
    }

    @Test
    void productCreateRequest_withInvalidPrice_failsValidation() {
        ProductCreateRequest request =
                new ProductCreateRequest("Valid Name", BigDecimal.valueOf(-1), 10);

        Set<ConstraintViolation<ProductCreateRequest>> violations = validate(request);

        assertTrue(hasViolationForProperty(violations, "price"));
    }

    @Test
    void productCreateRequest_withInvalidStock_failsValidation() {
        ProductCreateRequest request =
                new ProductCreateRequest("Valid Name", BigDecimal.valueOf(10000), -1);

        Set<ConstraintViolation<ProductCreateRequest>> violations = validate(request);

        assertTrue(hasViolationForProperty(violations, "stock"));
    }

    @Test
    void productUpdateRequest_withValidValues_passesValidation() {
        ProductUpdateRequest request =
                new ProductUpdateRequest("Valid Name", BigDecimal.valueOf(10000));

        Set<ConstraintViolation<ProductUpdateRequest>> violations = validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void productUpdateRequest_withBlankName_failsValidation() {
        ProductUpdateRequest request =
                new ProductUpdateRequest(" ", BigDecimal.valueOf(10000));

        Set<ConstraintViolation<ProductUpdateRequest>> violations = validate(request);

        assertTrue(hasViolationForProperty(violations, "name"));
    }

    @Test
    void productUpdateRequest_withInvalidPrice_failsValidation() {
        ProductUpdateRequest request =
                new ProductUpdateRequest("Valid Name", BigDecimal.ZERO);

        Set<ConstraintViolation<ProductUpdateRequest>> violations = validate(request);

        assertTrue(hasViolationForProperty(violations, "price"));
    }

    @Test
    void inventoryUpdateRequest_withInvalidStock_failsValidation() {
        InventoryUpdateRequest request =
                new InventoryUpdateRequest(-1);

        Set<ConstraintViolation<InventoryUpdateRequest>> violations = validate(request);

        assertTrue(hasViolationForProperty(violations, "stock"));
    }

    private boolean hasViolationForProperty(
            Set<? extends ConstraintViolation<?>> violations,
            String propertyName
    ) {
        return violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(propertyName));
    }

    private <T> Set<ConstraintViolation<T>> validate(T request) {
        return validator.validate(request);
    }
}
