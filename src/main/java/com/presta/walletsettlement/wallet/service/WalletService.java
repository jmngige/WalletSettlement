package com.presta.walletsettlement.wallet.service;

import com.presta.walletsettlement.rabbbitmq.TransactionMessagePublisher;
import com.presta.walletsettlement.wallet.domain.dto.LedgerTransaction;
import com.presta.walletsettlement.wallet.domain.dto.request.ConsumeRequest;
import com.presta.walletsettlement.wallet.domain.dto.request.TopUpRequest;
import com.presta.walletsettlement.wallet.domain.dto.response.BalanceResponse;
import com.presta.walletsettlement.wallet.domain.dto.response.ConsumeResponse;
import com.presta.walletsettlement.wallet.domain.dto.response.TopUpResponse;
import com.presta.walletsettlement.wallet.domain.model.Ledger;
import com.presta.walletsettlement.wallet.domain.model.Wallet;
import com.presta.walletsettlement.wallet.exception.DuplicateTransactionException;
import com.presta.walletsettlement.wallet.exception.InsufficientFundsException;
import com.presta.walletsettlement.wallet.exception.WalletNotFoundException;
import com.presta.walletsettlement.wallet.repo.LedgerRepository;
import com.presta.walletsettlement.wallet.repo.WalletRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class WalletService {

    private final LedgerRepository ledgerRepository;
    private final WalletRepository walletRepository;
    private final TransactionMessagePublisher messagePublisher;

    public WalletService(LedgerRepository ledgerRepository, WalletRepository walletRepository, TransactionMessagePublisher messagePublisher) {
        this.ledgerRepository = ledgerRepository;
        this.walletRepository = walletRepository;
        this.messagePublisher = messagePublisher;
    }

    @Transactional(rollbackFor = Exception.class)
    public TopUpResponse topUp(String walletId, @Valid TopUpRequest request) {
        //prevent reprocessing transactions on retries
        boolean exists = ledgerRepository.existsByTransactionRequestId(request.getRequestId());
        if (exists) {
            throw new DuplicateTransactionException("Transaction already processed");
        }

        // Fetch wallet with pessimistic lock
        Wallet wallet = walletRepository.findByWalletId(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        //update wallet balance
        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        //create ledger transaction on topup
        String description = String.format("Wallet top-up with %s", request.getAmount());
        LedgerTransaction ledgerTxn = LedgerTransaction.builder()
                .amount(request.getAmount())
                .walletId(walletId)
                .transactionId(request.getTransactionId())
                .transactionRequestId(request.getRequestId())
                .description(description)
                .transactionDate(LocalDate.now())
                .build();

        Ledger transaction = createLedgerTransaction(ledgerTxn);
        ledgerRepository.save(transaction);

        //queue the topup on queue
        messagePublisher.publishTransactionMessage(ledgerTxn);

        return new TopUpResponse("Wallet top-up successful", newBalance, "COMPLETED", LocalDateTime.now());

    }

    @Transactional(rollbackFor = Exception.class)
    public ConsumeResponse consume(String walletId, @Valid ConsumeRequest request) {
        //prevent reprocessing transactions on retries
        boolean exists = ledgerRepository.existsByTransactionRequestId(request.getRequestId());
        if (exists) {
            throw new DuplicateTransactionException("Transaction already processed");
        }

        // Fetch wallet with pessimistic lock
        Wallet wallet = walletRepository.findByWalletId(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        // Check sufficient balance
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient wallet funds to process your request");
        }

        //update wallet balance
        BigDecimal newBalance = wallet.getBalance().subtract(request.getAmount());
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        //create ledger transaction on consume
        String description = String.format("Consuming for %s", request.getAmount());
        LedgerTransaction ledgerTxn = LedgerTransaction.builder()
                .amount(request.getAmount())
                .walletId(walletId)
                .transactionId(request.getTransactionId())
                .transactionRequestId(request.getRequestId())
                .description(description)
                .transactionDate(LocalDate.now())
                .build();

        Ledger transaction = createLedgerTransaction(ledgerTxn);
        ledgerRepository.save(transaction);

        //queue the consume on queue
        messagePublisher.publishTransactionMessage(ledgerTxn);

        return new ConsumeResponse("Request processed successfully", request.getRequestId(), "COMPLETED", LocalDateTime.now());
    }

    private Ledger createLedgerTransaction(LedgerTransaction transaction) {
        Ledger txn = new Ledger();
        txn.setWalletId(transaction.getWalletId());
        txn.setAmount(transaction.getAmount());
        txn.setTransactionRequestId(transaction.getTransactionRequestId());
        txn.setTransactionId(transaction.getTransactionId());
        txn.setDescription(transaction.getDescription());
        txn.setTransactionDate(transaction.getTransactionDate());
        return txn;
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long id) {
        return walletRepository.findById(id)
                .map(wallet ->
                        new BalanceResponse(wallet.getId(), wallet.getBalance()))
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }
}
