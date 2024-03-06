package com.server.oceankeeper.global.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String errorMessage;
    private final String errorDetail;
    private final ErrorCode errorCode;
}
