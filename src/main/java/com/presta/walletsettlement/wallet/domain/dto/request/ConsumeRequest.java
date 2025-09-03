package com.presta.walletsettlement.wallet.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to consume a wallet")
public class ConsumeRequest {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotBlank(message = "Request ID is required")
    private String customerId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "service call is required")
    private String service;
}
