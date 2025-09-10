package com.presta.walletsettlement.wallet.domain.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(String walletId, BigDecimal balance) {
}
