package com.presta.walletsettlement.wallet.domain.model;

import com.presta.walletsettlement.wallet.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ledger_transactions")
@NoArgsConstructor
@AllArgsConstructor
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long walletId;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private TransactionType tranType;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
