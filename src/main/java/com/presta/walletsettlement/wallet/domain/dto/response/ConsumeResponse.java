package com.presta.walletsettlement.wallet.domain.dto.response;
import java.time.LocalDateTime;

public record ConsumeResponse(String message, String requestId, String service, String status, LocalDateTime timestamp) {
}
