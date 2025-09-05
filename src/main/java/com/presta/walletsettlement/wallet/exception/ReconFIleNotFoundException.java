package com.presta.walletsettlement.wallet.exception;

import com.presta.walletsettlement.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReconFIleNotFoundException extends ApiException {
    public ReconFIleNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, "RECON_FILE_NOT_FOUND");
    }
}
