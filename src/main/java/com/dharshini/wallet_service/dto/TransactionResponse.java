package com.dharshini.wallet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private UUID transactionId;

    private UUID userId;

    private BigDecimal balance;

    private String message;
}