package com.presta.walletsettlement.wallet.service;


import com.presta.walletsettlement.rabbbitmq.TransactionMessagePublisher;
import com.presta.walletsettlement.wallet.domain.dto.LedgerTransaction;
import com.presta.walletsettlement.wallet.domain.dto.request.ConsumeRequest;
import com.presta.walletsettlement.wallet.domain.dto.request.TopUpRequest;
import com.presta.walletsettlement.wallet.domain.dto.response.TopUpResponse;
import com.presta.walletsettlement.wallet.domain.model.Wallet;
import com.presta.walletsettlement.wallet.exception.DuplicateTransactionException;
import com.presta.walletsettlement.wallet.exception.InsufficientFundsException;
import com.presta.walletsettlement.wallet.exception.WalletNotFoundException;
import com.presta.walletsettlement.wallet.repo.LedgerRepository;
import com.presta.walletsettlement.wallet.repo.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private LedgerRepository ledgerRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionMessagePublisher messagePublisher;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletId("WLT-001");
        wallet.setBalance(new BigDecimal("100.00"));
        wallet.setCreatedAt(LocalDate.now());
    }


    @Test
    void topUp_success_updatesBalance_createsLedger_and_publishesMessage() {
        TopUpRequest req = TopUpRequest.builder()
                .requestId("req-MP-001")
                .transactionId("ref-MP-001")
                .amount(new BigDecimal("50.00"))
                .build();

        when(ledgerRepository.existsByTransactionRequestId("req-MP-001")).thenReturn(false);
        when(walletRepository.findByWalletId("WLT-001")).thenReturn(java.util.Optional.of(wallet));
        when(ledgerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TopUpResponse resp = walletService.topUp("WLT-001", req);

        assertNotNull(resp);
        assertEquals("Wallet top-up successful", resp.message());
        assertEquals(0, resp.balance().compareTo(new BigDecimal("150.00")));
        verify(walletRepository).save(ArgumentMatchers.argThat(w -> w.getBalance().compareTo(new BigDecimal("150.00")) == 0));
        verify(ledgerRepository).save(any());
        ArgumentCaptor<LedgerTransaction> captor = ArgumentCaptor.forClass(LedgerTransaction.class);
        verify(messagePublisher).publishTransactionMessage(captor.capture());
        LedgerTransaction published = captor.getValue();
        assertEquals("req-MP-001", published.getTransactionRequestId());
        assertEquals("ref-MP-001", published.getTransactionId());
        assertEquals("WLT-001", published.getWalletId());
    }

    @Test
    void topUp_duplicate_throwsDuplicateTransactionException() {
        TopUpRequest req = TopUpRequest.builder()
                .requestId("req-MP-001")
                .transactionId("ref-MP-001")
                .amount(new BigDecimal("10.00"))
                .build();
        when(ledgerRepository.existsByTransactionRequestId("req-MP-001")).thenReturn(true);

        assertThrows(DuplicateTransactionException.class, () -> walletService.topUp("WLT-001", req));
        verify(walletRepository, never()).findByWalletId(any());
        verify(ledgerRepository, never()).save(any());
        verify(messagePublisher, never()).publishTransactionMessage(any());
    }

    @Test
    void topUp_walletNotFound_throwsWalletNotFoundException() {
        TopUpRequest req = TopUpRequest.builder()
                .requestId("req-MP-100")
                .transactionId("ref-MP-100")
                .amount(new BigDecimal("10.00"))
                .build();
        when(ledgerRepository.existsByTransactionRequestId("req-MP-100")).thenReturn(false);
        when(walletRepository.findByWalletId("WLT-100")).thenReturn(java.util.Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.topUp("WLT-100", req));
        verify(ledgerRepository, never()).save(any());
        verify(messagePublisher, never()).publishTransactionMessage(any());
    }

    @Test
    void consume_success_updatesBalance_createsLedger_and_publishesMessage() {
        ConsumeRequest req = ConsumeRequest.builder()
                .requestId("req-KYC-001")
                .transactionId("ref-KYC-001")
                .amount(new BigDecimal("40.00"))
                .build();

        when(ledgerRepository.existsByTransactionRequestId("req-KYC-001")).thenReturn(false);
        when(walletRepository.findByWalletId("WLT-001")).thenReturn(java.util.Optional.of(wallet));
        when(ledgerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var resp = walletService.consume("WLT-001", req);

        assertNotNull(resp);
        assertEquals("Request processed successfully", resp.message());
        assertEquals("req-KYC-001", resp.requestId());
        verify(walletRepository).save(ArgumentMatchers.argThat(w -> w.getBalance().compareTo(new BigDecimal("60.00")) == 0));
        verify(ledgerRepository).save(any());
        verify(messagePublisher).publishTransactionMessage(any());
    }

    @Test
    void consume_insufficientFunds_throwsInsufficientFundsException() {
        ConsumeRequest req = ConsumeRequest.builder()
                .requestId("req-KYC-001")
                .transactionId("ref-KYC-001")
                .amount(new BigDecimal("200.00"))
                .build();

        when(ledgerRepository.existsByTransactionRequestId("req-KYC-001")).thenReturn(false);
        when(walletRepository.findByWalletId("WLT-001")).thenReturn(java.util.Optional.of(wallet));

        assertThrows(InsufficientFundsException.class, () -> walletService.consume("WLT-001", req));
        verify(walletRepository, never()).save(any());
        verify(ledgerRepository, never()).save(any());
        verify(messagePublisher, never()).publishTransactionMessage(any());
    }

    @Test
    void getBalance_success_returnsBalance() {
        when(walletRepository.findByWalletId("WLT-001")).thenReturn(java.util.Optional.of(wallet));
        var resp = walletService.getBalance("WLT-001");
        assertNotNull(resp);
        assertEquals("WLT-001", resp.walletId());
        assertEquals(0, resp.balance().compareTo(new BigDecimal("100.00")));
    }

    @Test
    void getBalance_walletNotFound_throws() {
        when(walletRepository.findByWalletId("WLT-002")).thenReturn(java.util.Optional.empty());
        assertThrows(WalletNotFoundException.class, () -> walletService.getBalance("WLT-002"));
    }

}