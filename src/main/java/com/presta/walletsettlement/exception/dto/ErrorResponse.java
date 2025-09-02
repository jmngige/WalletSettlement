package com.presta.walletsettlement.exception.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String message;
    private String errorCode;
}
