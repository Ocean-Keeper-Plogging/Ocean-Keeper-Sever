package com.server.oceankeeper.Global.Exception;

public class DuplicatedResourceException extends RuntimeException{

    public DuplicatedResourceException(String message) {
        super(message);
    }
}
