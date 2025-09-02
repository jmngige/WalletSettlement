package com.presta.walletsettlement.wallet.domain.dto;

import com.presta.walletsettlement.wallet.domain.enums.TransactionSource;
import com.presta.walletsettlement.wallet.domain.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LedgerTransaction {
    private Long walletId;
    private String customerId;
    private BigDecimal amount;
    private TransactionType tranType;
    private String transactionRequestId;
    private String transactionReference;
    private TransactionSource source;
    private String description;
    private LocalDateTime transactionDate;
}
