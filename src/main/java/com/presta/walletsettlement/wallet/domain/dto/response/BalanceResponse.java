package com.presta.walletsettlement.wallet.domain.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceResponse {
    private String walletId;
    private String customerId;
    private BigDecimal balance;
}
