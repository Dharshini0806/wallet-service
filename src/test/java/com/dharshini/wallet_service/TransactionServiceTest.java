package com.dharshini.wallet_service;

import com.dharshini.wallet_service.dto.TransactionRequest;
import com.dharshini.wallet_service.dto.TransactionResponse;
import com.dharshini.wallet_service.entity.TransactionType;
import com.dharshini.wallet_service.entity.Wallet;
import com.dharshini.wallet_service.repository.WalletRepository;
import com.dharshini.wallet_service.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void shouldDebitWalletSuccessfully() {

        UUID userId = UUID.randomUUID();

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(new BigDecimal("1000"))
                .build();

        walletRepository.save(wallet);

        TransactionRequest request = TransactionRequest.builder()
                .transactionId(UUID.randomUUID())
                .userId(userId)
                .amount(new BigDecimal("200"))
                .type(TransactionType.DEBIT)
                .build();

        TransactionResponse response =
                transactionService.processTransaction(request);

        assertEquals(0, response.getBalance().compareTo(new BigDecimal("800.00")));
    }
}