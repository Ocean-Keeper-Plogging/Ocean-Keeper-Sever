package com.server.oceankeeper.global.exception;

public class IdNotFoundException extends IllegalArgumentException{
    public IdNotFoundException(String message){
        super(message);
    }
}
