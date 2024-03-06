package com.server.oceankeeper.global.exception;

public class JwtExpireException extends RuntimeException {
    public JwtExpireException(String s) {
        super(s);
    }
}
