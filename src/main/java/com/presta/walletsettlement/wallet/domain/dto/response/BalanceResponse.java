package com.presta.walletsettlement.wallet.domain.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(Long walletId, BigDecimal balance) {
}
