package com.dharshini.wallet_service.service;

import com.dharshini.wallet_service.dto.TransactionRequest;
import com.dharshini.wallet_service.dto.TransactionResponse;
import com.dharshini.wallet_service.entity.Transaction;
import com.dharshini.wallet_service.entity.TransactionType;
import com.dharshini.wallet_service.entity.Wallet;
import com.dharshini.wallet_service.exception.DuplicateTransactionException;
import com.dharshini.wallet_service.exception.InsufficientBalanceException;
import com.dharshini.wallet_service.repository.TransactionRepository;
import com.dharshini.wallet_service.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(WalletRepository walletRepository,
                              TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {

        // Check duplicate transaction
        if (transactionRepository.existsByTransactionId(request.getTransactionId())) {
            throw new DuplicateTransactionException("Transaction already processed");
        }

        // Get wallet with database lock
        Wallet wallet = walletRepository.findWithLockByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Process transaction
        if (request.getType() == TransactionType.DEBIT) {

            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }

            wallet.debit(request.getAmount());

        } else {

            wallet.credit(request.getAmount());
        }

        // Save updated wallet
        walletRepository.save(wallet);

        // Save transaction
        Transaction transaction = Transaction.builder()
                .transactionId(request.getTransactionId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .type(request.getType())
                .processedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        // Return response
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .message("Transaction processed successfully")
                .build();
    }
}