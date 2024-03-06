package com.server.oceankeeper.global.exception;

public class IllegalRequestException extends IllegalArgumentException{

    public  IllegalRequestException (String message) {
        super(message);
    }
}
