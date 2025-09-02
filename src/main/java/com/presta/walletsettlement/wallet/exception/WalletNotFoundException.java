package com.presta.walletsettlement.wallet.exception;

import com.presta.walletsettlement.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WalletNotFoundException extends ApiException {
    public WalletNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, "WALLET_NOT_FOUND");
    }
}
