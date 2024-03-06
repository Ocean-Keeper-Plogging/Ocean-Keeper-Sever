package com.server.oceankeeper.global.exception;

public class NotValidFormatException extends IllegalRequestException{
    public NotValidFormatException(String message) {
        super(message);
    }
}
