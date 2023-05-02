package com.server.oceankeeper.Global.Exception;

public class IllegalRequestException extends IllegalArgumentException{

    public  IllegalRequestException (String message) {
        super(message);
    }
}
