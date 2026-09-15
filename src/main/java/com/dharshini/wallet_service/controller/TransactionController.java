package com.dharshini.wallet_service.controller;

import com.dharshini.wallet_service.dto.TransactionRequest;
import com.dharshini.wallet_service.dto.TransactionResponse;
import com.dharshini.wallet_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/process")
    public ResponseEntity<TransactionResponse> processTransaction(
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponse response =
                transactionService.processTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}