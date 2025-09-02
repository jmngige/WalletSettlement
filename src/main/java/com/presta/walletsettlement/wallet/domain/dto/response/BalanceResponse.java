package com.presta.walletsettlement.wallet.domain.dto.response;

import java.math.BigDecimal;


public record BalanceResponse(String walletId, String customerId, BigDecimal balance) {
}
