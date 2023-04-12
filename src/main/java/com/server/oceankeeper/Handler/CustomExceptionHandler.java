package com.server.oceankeeper.Handler;

import com.server.oceankeeper.DTO.ResponseDto;
import com.server.oceankeeper.Exception.CustomApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(new ResponseDto(-1, e.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
