package org.example.ecommerceapi.domain.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthRequestValidationTest {

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
    void registerRequest_withValidValues_passesValidation() {
        var request = new RegisterRequest("test@example.com", "password");
        var violations = validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void registerRequest_withInvalidEmail_failsValidation() {
        var request = new RegisterRequest("invalid-email", "password");
        var violations = validate(request);
        assertTrue(hasViolationForProperty(violations, "email"));
    }

    @Test
    void registerRequest_withBlankPassword_failsValidation() {
        var request = new RegisterRequest("test@example.com", "");
        var violations = validate(request);
        assertTrue(hasViolationForProperty(violations, "password"));
    }

    @Test
    void loginRequest_withValidValues_passesValidation() {
        var request = new LoginRequest("test@example.com", "password");
        var violations = validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void loginRequest_withBlankValues_failsValidation() {
        var request = new LoginRequest("", "");
        var violations = validate(request);
        assertTrue(hasViolationForProperty(violations, "email"));
        assertTrue(hasViolationForProperty(violations, "password"));
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
