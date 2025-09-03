package com.presta.walletsettlement.wallet.domain.model;

import com.presta.walletsettlement.wallet.domain.enums.TransactionSource;
import com.presta.walletsettlement.wallet.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ledger_transactions", indexes = {
        @Index(name = "idx_ledger_wallet", columnList = "wallet_id"),
        @Index(name = "idx_ledger_tx_request", columnList = "transaction_request_id")
})
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "amount",precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "tran_type",nullable = false)
    private TransactionType tranType;

    @Column(name = "transaction_request_id",nullable = false, unique = true)
    private String transactionRequestId;

    @Column(name = "trnsaction_reference", nullable = false)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private TransactionSource source;

    @Column(name = "description", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

}
