package org.example.ecommerceapi.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionTest {

    @Test
    void constructor_storesErrorCode() {
        BusinessException exception = new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);

        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
