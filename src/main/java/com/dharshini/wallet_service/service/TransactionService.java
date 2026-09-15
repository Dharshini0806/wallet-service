package com.dharshini.wallet_service.service;

import com.dharshini.wallet_service.dto.TransactionRequest;
import com.dharshini.wallet_service.repository.TransactionRepository;
import com.dharshini.wallet_service.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public String processTransaction(TransactionRequest request) {
        return "Processing...";
    }
}