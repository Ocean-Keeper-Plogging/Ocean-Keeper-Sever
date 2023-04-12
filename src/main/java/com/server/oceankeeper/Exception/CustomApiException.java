package com.server.oceankeeper.Exception;

public class CustomApiException extends RuntimeException{

    public CustomApiException(String message) {
        super(message);
    }
}
