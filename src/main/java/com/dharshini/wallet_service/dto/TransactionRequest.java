package com.dharshini.wallet_service.dto;

import com.dharshini.wallet_service.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull
    private UUID transactionId;

    @NotNull
    private UUID userId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private TransactionType type;
}