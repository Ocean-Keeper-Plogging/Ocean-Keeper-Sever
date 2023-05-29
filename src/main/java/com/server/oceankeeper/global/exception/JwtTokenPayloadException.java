package com.server.oceankeeper.global.exception;

public class JwtTokenPayloadException extends RuntimeException {
    public JwtTokenPayloadException(String s) {
        super(s);
    }
}
