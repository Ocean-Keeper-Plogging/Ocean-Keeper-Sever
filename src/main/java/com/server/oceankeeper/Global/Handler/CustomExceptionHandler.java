package com.server.oceankeeper.Global.Handler;

import com.amazonaws.Response;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.server.oceankeeper.Global.Exception.DtoValidationException;
import com.server.oceankeeper.Global.Exception.DuplicatedResourceException;
import com.server.oceankeeper.Global.Exception.IdNotFoundException;
import com.server.oceankeeper.Global.Exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @ExceptionHandler(IllegalRequestException.class)
    public ResponseEntity<String> IllegalRequestException(IllegalRequestException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> runtimeException(RuntimeException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<String> idNotFoundException(IdNotFoundException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicatedResourceException.class)
    public ResponseEntity<String> duplicatedIdException(DuplicatedResourceException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<String> S3ArgumentException(AmazonS3Exception e){
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DtoValidationException.class)
    public ResponseEntity<String> validationApiException(DtoValidationException e){

        return new ResponseEntity<>(e.getMessage()+": "+e.getErrorMap().toString(),HttpStatus.BAD_REQUEST);
    }
}
