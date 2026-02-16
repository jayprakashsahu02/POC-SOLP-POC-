package com.stocksync.order_service.controller;

import com.stocksync.order_service.dto.TransactionRequestDTO;
import com.stocksync.order_service.dto.TransactionResponseDTO;
import com.stocksync.order_service.dto.TransactionStatusUpdateDTO;
import com.stocksync.order_service.model.TransactionType;
import com.stocksync.order_service.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
// Import for HttpServletRequest
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin
class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    // Define the expected header name once
    private static final String USER_ID_HEADER = "X-Auth-User-Id";

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // --- Utility method to extract and validate User ID ---
    private String getUserId(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID_HEADER);
        if (userId == null || userId.isEmpty()) {
            // Throwing a runtime exception will result in a 500 or 400 error response.
            // In a production setup, this would usually be handled by a filter/interceptor
            // and return a 401/403 before reaching the controller.
            throw new IllegalArgumentException("Authentication context missing. Header '" + USER_ID_HEADER + "' not found.");
        }
        return userId;
    }
    // ------------------------------------------------------


    /**
     * POST /api/v1/transactions/sell - Creates a new SELL order.
     */
    @PostMapping("/sell")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO createSellOrder(
            @Validated
            @RequestBody TransactionRequestDTO requestDTO,
            HttpServletRequest request // Inject HttpServletRequest
    ) {
        // 1. Get user ID from header
        String userId = getUserId(request);

        // 2. Pass the user ID to the service method
        return transactionService.createTransaction(requestDTO, TransactionType.SELL, userId);
    }

    /**
     * POST /api/v1/transactions/purchase - Creates a new PURCHASE order.
     */
    @PostMapping("/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO createPurchaseOrder(
            @Validated
            @RequestBody TransactionRequestDTO requestDTO,
            HttpServletRequest request // Inject HttpServletRequest
    ) {
        // 1. Get user ID from header
        String userId = getUserId(request);

        System.out.println("Received PURCHASE order request for product ID: " + requestDTO.productId() + " by User: " + userId);

        // 2. Pass the user ID to the service method
        return transactionService.createTransaction(requestDTO, TransactionType.PURCHASE, userId);
    }

    // --- Other methods (GET/PATCH) do not need the current user ID for creation/lookup,
    // but the PATCH endpoint could log who updated the status. ---

    /**
     * GET /api/v1/transactions - Fetches all transactions.
     */
    @GetMapping
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionService.findAllTransactions();
    }

    /**
     * GET /api/v1/transactions/{id} - Fetches a transaction by ID.
     */
    @GetMapping("/{id}")
    public TransactionResponseDTO getTransactionById(@PathVariable BigInteger id) {
        return transactionService.findTransactionById(id);
    }

    /**
     * PATCH /api/v1/transactions/{id}/status
     * This endpoint is designed to be called by the Stock Microservice
     * to update the status once stock operations are complete/cancelled.
     */
    @PatchMapping("/{id}/status")
    public TransactionResponseDTO updateTransactionStatus(
            @PathVariable BigInteger id,
            @RequestBody TransactionStatusUpdateDTO statusUpdateDTO,
            HttpServletRequest request // Inject to record who updated the status (for audit)
    ) {
        // Since this is likely called by another service (Stock Microservice) or an admin,
        // you should pass the current user ID (or a service identifier) for auditing.
        String updatingUserId = getUserId(request); // Will throw if header is missing

        log.info("Received status update for Transaction ID: {} to {} by User/Service: {}", id, statusUpdateDTO.newStatus(), updatingUserId);

        // You'll need to update the service signature for this one as well if you want
        // to persist the 'updatedBy' user ID.
        return transactionService.updateTransactionStatus(id, statusUpdateDTO, updatingUserId);
    }
}