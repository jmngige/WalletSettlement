package com.presta.walletsettlement.wallet.service;

import com.presta.walletsettlement.wallet.domain.dto.request.TopUpRequest;
import com.presta.walletsettlement.wallet.domain.dto.response.TopUpResponse;
import com.presta.walletsettlement.wallet.domain.enums.TransactionSource;
import com.presta.walletsettlement.wallet.domain.enums.TransactionType;
import com.presta.walletsettlement.wallet.domain.model.Ledger;
import com.presta.walletsettlement.wallet.domain.model.Wallet;
import com.presta.walletsettlement.wallet.exception.DuplicateTransactionException;
import com.presta.walletsettlement.wallet.exception.WalletNotFoundException;
import com.presta.walletsettlement.wallet.repo.LedgerRepository;
import com.presta.walletsettlement.wallet.repo.WalletRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class WalletService {

    private final LedgerRepository ledgerRepository;
    private final WalletRepository walletRepository;

    public WalletService(LedgerRepository ledgerRepository, WalletRepository walletRepository) {
        this.ledgerRepository = ledgerRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public TopUpResponse topUp(String customerId, @Valid TopUpRequest request) {
        //prevent reprocessing transactions on retries
        boolean exists = ledgerRepository.existsByTransactionRequestId(request.getRequestId());
        if (!exists) {
            throw new DuplicateTransactionException("Transaction already processed");
        }

        //fetch customer wallet
        Wallet wallet = walletRepository.findByCustomerId(customerId).orElseThrow(
                () -> new WalletNotFoundException("Wallet not found")
        );

        //update wallet balance
        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        //create ledger transaction on topup
        Ledger transaction = createLedgerTransaction(customerId, TransactionType.CREDIT, TransactionSource.EXTERNAL, request);
        ledgerRepository.save(transaction);

        //TODO queue the topup on queue

        return new TopUpResponse("Wallet top-up successful", newBalance, "COMPLETED", LocalDateTime.now());

    }

    private Ledger createLedgerTransaction(String customerId, TransactionType tranType, TransactionSource source, TopUpRequest request) {
        Ledger transaction = new Ledger();
        transaction.setCustomerId(customerId);
        transaction.setAmount(request.getAmount());
        transaction.setTranType(tranType);
        transaction.setTransactionRequestId(request.getRequestId());
        transaction.setTransactionReference(request.getTransactionId());
        transaction.setSource(source);
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(LocalDateTime.now());

        return transaction;
    }


}
