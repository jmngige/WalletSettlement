package com.presta.walletsettlement.wallet.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TopUpResponse(String message, BigDecimal balance, String status, LocalDateTime timestamp) {
}
