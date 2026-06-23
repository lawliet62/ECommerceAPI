package org.example.ecommerceapi.domain.cart.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartRequestValidationTest {

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
    void addItemRequest_withValidValues_passesValidation() {
        var request = new AddItemRequest(1L, 1);
        var violations = validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void addItemRequest_withInvalidProductId_failsValidation() {
        var request = new AddItemRequest(0L, 1);
        var violations = validate(request);
        assertTrue(hasViolationForProperty(violations, "productId"));
    }

    @Test
    void addItemRequest_withInvalidQuantity_failsValidation() {
        var request = new AddItemRequest(1L, 0);
        var violations = validate(request);
        assertTrue(hasViolationForProperty(violations, "quantity"));
    }

    @Test
    void updateItemRequest_withValidQuantity_passesValidation() {
        var request = new UpdateItemRequest(1);
        var violations = validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void updateItemRequest_withInvalidQuantity_failsValidation() {
        var request = new UpdateItemRequest(0);
        var violations = validate(request);
        assertTrue(hasViolationForProperty(violations, "quantity"));
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
