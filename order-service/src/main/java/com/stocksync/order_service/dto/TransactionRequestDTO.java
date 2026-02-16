package com.stocksync.order_service.dto;

import com.stocksync.order_service.exception.InvalidTransactionException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
// Note: You will need to create the InvalidTransactionRequestException class


public record TransactionRequestDTO(
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        Long productId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        int quantity
) {}
