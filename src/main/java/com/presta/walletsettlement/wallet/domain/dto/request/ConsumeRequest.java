package com.presta.walletsettlement.wallet.domain.dto.request;

import lombok.Data;

@Data
public class ConsumeRequest {
    private String requestId;
    private String transactionId;
    private String amount;
    private String service;
    private String description;
}
