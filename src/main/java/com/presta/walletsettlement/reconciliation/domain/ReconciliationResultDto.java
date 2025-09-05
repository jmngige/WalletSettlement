package com.presta.walletsettlement.reconciliation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationResultDto {
    private String reconType;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private String description;
}
