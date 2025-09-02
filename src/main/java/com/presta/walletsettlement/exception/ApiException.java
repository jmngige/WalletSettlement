package com.presta.walletsettlement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public ApiException(HttpStatus status, String message, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
