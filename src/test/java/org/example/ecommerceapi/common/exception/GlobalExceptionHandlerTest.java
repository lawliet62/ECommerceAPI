package org.example.ecommerceapi.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument_returnsBadRequestResponse() {
        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgument(new IllegalArgumentException("Invalid request"));

        assertErrorResponse(
                response,
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                "Invalid request"
        );
    }

    @Test
    void handleValidation_returnsValidationErrorResponse() {
        MethodArgumentNotValidException exception =
                createMethodArgumentNotValidException();

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception);

        assertErrorResponse(
                response,
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "email: must be a well-formed email address"
        );
    }

    @Test
    void handleBusiness_returnsErrorCodeResponse() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBusiness(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        assertErrorResponse(
                response,
                HttpStatus.NOT_FOUND,
                "PRODUCT_NOT_FOUND",
                "Product not found"
        );
    }

    private MethodArgumentNotValidException createMethodArgumentNotValidException() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new TestRequest("invalid-email"), "request");
        bindingResult.addError(new FieldError(
                "request",
                "email",
                "must be a well-formed email address"
        ));

        return new MethodArgumentNotValidException(
                mock(MethodParameter.class),
                bindingResult
        );
    }

    private void assertErrorResponse(
            ResponseEntity<ErrorResponse> response,
            HttpStatus status,
            String code,
            String message
    ) {
        assertEquals(status, response.getStatusCode());
        ErrorResponse body = assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals(code, body.code());
        assertEquals(message, body.message());
    }

    private record TestRequest(String email) {
    }
}
