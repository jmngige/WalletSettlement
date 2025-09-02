package com.presta.walletsettlement.wallet.exception;

import com.presta.walletsettlement.exception.ApiException;
import org.springframework.http.HttpStatus;

public class DuplicateTransactionException extends ApiException {
    public DuplicateTransactionException(String message) {
        super(HttpStatus.CONFLICT, message, "DUPLICATE_TRANSACTION");
    }
}
