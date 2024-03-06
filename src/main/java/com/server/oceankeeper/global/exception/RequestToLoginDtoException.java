package com.server.oceankeeper.global.exception;

public class RequestToLoginDtoException extends RuntimeException {
    public RequestToLoginDtoException(String message) {
        super(message);
    }
}
