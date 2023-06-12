package com.server.oceankeeper.global.handler;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.server.oceankeeper.global.exception.*;
import com.server.oceankeeper.global.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(IllegalRequestException.class)
    public ResponseEntity<ApiResponse> illegalRequestException(IllegalRequestException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> runtimeException(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.createError("서버 에러"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponse.createError("해당 메소드를 지원하지 않습니다. 파라미터를 확인해주세요."));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.createError("media type이 맞지 않습니다. application/json으로 설정해주세요"));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiResponse> internalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> httpMessageNotReadableException(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError("request JSON 파싱 에러. request body를 확인해주세요."));
    }

    @ExceptionHandler(UuidValidException.class)
    public ResponseEntity<ApiResponse> uuidValidException(UuidValidException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError("요청 id 에러"));
    }

    @ExceptionHandler(JwtTokenPayloadException.class)
    public ResponseEntity<ApiResponse> jwtTokenPayloadException(JwtTokenPayloadException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.createError("JWT 토큰 파싱 에러"));
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<ApiResponse> idNotFoundException(IdNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> forbiddenException(ForbiddenException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(DuplicatedResourceException.class)
    public ResponseEntity<ApiResponse> duplicatedIdException(DuplicatedResourceException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<ApiResponse> S3ArgumentException(AmazonS3Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(DtoValidationException.class)
    public ResponseEntity<ApiResponse> validationApiException(DtoValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createError(e.getMessage() + ": " + e.getErrorMap().toString()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundException(ResourceNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ApiResponse> expiredTokenException(ExpiredTokenException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.createError(e.getMessage()));
    }
}
