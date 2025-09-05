package com.presta.walletsettlement.wallet.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ledger_transactions", indexes = {
        @Index(name = "idx_ledger_transaction_date", columnList = "transaction_date")
})
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_request_id", nullable = false, unique = true)
    private String transactionRequestId;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDate transactionDate;
}
