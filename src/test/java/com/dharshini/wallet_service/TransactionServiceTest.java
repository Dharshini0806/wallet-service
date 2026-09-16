package com.dharshini.wallet_service;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.dharshini.wallet_service.dto.TransactionRequest;
import com.dharshini.wallet_service.dto.TransactionResponse;
import com.dharshini.wallet_service.entity.TransactionType;
import com.dharshini.wallet_service.entity.Wallet;
import com.dharshini.wallet_service.repository.WalletRepository;
import com.dharshini.wallet_service.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Processes a single valid debit transaction successfully")
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
    @Test
    @DisplayName("Processes a single valid credit transaction successfully")
    void shouldCreditWalletSuccessfully() {

        UUID userId = UUID.randomUUID();

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(new BigDecimal("1000"))
                .build();

        walletRepository.save(wallet);

        TransactionRequest request = TransactionRequest.builder()
                .transactionId(UUID.randomUUID())
                .userId(userId)
                .amount(new BigDecimal("300"))
                .type(TransactionType.CREDIT)
                .build();

        TransactionResponse response =
                transactionService.processTransaction(request);

        assertEquals(new BigDecimal("1300.00"), response.getBalance());
    }
    @Test
    @DisplayName("Sends 3 identical transaction IDs simultaneously. Ensures the balance is only deducted once.")
    void shouldProcessDuplicateTransactionOnlyOnce() throws Exception {

        UUID userId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(new BigDecimal("1000"))
                .build();

        walletRepository.save(wallet);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {

            executor.submit(() -> {

                try {

                    TransactionRequest request = TransactionRequest.builder()
                            .transactionId(transactionId)
                            .userId(userId)
                            .amount(new BigDecimal("100"))
                            .type(TransactionType.DEBIT)
                            .build();

                    try {
                        transactionService.processTransaction(request);
                    } catch (Exception ignored) {
                    }

                } finally {
                    latch.countDown();
                }

            });

        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        Wallet updatedWallet =
                walletRepository.findByUserId(userId).orElseThrow();

        assertEquals(0,
                updatedWallet.getBalance().compareTo(new BigDecimal("900.00")));
    }
    @Test
    @DisplayName("Sends 10 concurrent debit requests of ₹100 for a wallet with ₹500 balance. Ensures the final balance is exactly ₹0 and 5 requests fail with insufficient funds.")
    void shouldHandleConcurrentDebitRequests() throws Exception {

        UUID userId = UUID.randomUUID();

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(new BigDecimal("500"))
                .build();

        walletRepository.save(wallet);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < 10; i++) {

            executor.submit(() -> {

                try {

                    TransactionRequest request = TransactionRequest.builder()
                            .transactionId(UUID.randomUUID())
                            .userId(userId)
                            .amount(new BigDecimal("100"))
                            .type(TransactionType.DEBIT)
                            .build();

                    transactionService.processTransaction(request);

                    successCount.incrementAndGet();

                } catch (Exception e) {

                    failureCount.incrementAndGet();

                } finally {

                    latch.countDown();

                }

            });

        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        Wallet updatedWallet =
                walletRepository.findByUserId(userId).orElseThrow();

        assertEquals(0,
                updatedWallet.getBalance().compareTo(BigDecimal.ZERO));

        assertEquals(5, successCount.get());

        assertEquals(5, failureCount.get());
    }
}