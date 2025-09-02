package com.presta.walletsettlement.wallet.domain.dto.request;

import lombok.Data;

@Data
public class TopUpRequest {
    private String requestId;
    private String transactionId;
    private String amount;
    private String description;
    private String provider;
}
