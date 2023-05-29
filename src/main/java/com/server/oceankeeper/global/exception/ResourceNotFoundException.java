package com.server.oceankeeper.global.exception;

public class ResourceNotFoundException extends IllegalArgumentException{
    public ResourceNotFoundException(String s) {
        super(s);
    }
}
