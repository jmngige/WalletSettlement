package com.presta.walletsettlement.wallet.exception;

import com.presta.walletsettlement.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends ApiException {
    public InsufficientFundsException(String message) {
        super(HttpStatus.PAYMENT_REQUIRED, message, "INSUFFICIENT_FUNDS");
    }
}
