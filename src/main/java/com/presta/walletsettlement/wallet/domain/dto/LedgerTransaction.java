package com.presta.walletsettlement.wallet.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LedgerTransaction {
    private String walletId;
    private BigDecimal amount;
    private String type;
    private String transactionRequestId;
    private String transactionId;
    private String description;
    private LocalDate transactionDate;
}
