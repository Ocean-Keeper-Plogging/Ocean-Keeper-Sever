package com.server.oceankeeper.global.handler;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.server.oceankeeper.global.exception.*;
import com.server.oceankeeper.global.response.APIResponse;
import com.server.oceankeeper.global.response.ErrorCode;
import com.server.oceankeeper.global.response.ErrorResponse;
import org.apache.commons.fileupload.FileUploadBase;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ValidationException;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(DtoValidationException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> validationApiException(DtoValidationException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                e.getMessage() + ": " + e.getErrorMap().toString(),
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    public ResponseEntity<APIResponse<ErrorResponse>> validationException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "필수 파라미터가 잘못되었습니다.",
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler(NotValidFormatException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> notValidFormatException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> missingParamException(MissingServletRequestParameterException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "필수 파라미터가 누락되었습니다.",
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler(FileUploadBase.SizeLimitExceededException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> SizeLimitExceededException(FileUploadBase.SizeLimitExceededException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "파일 사이즈가 너무 큽니다. 다른 이미지를 사용해주세요",
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler(IllegalRequestException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> illegalRequestException(IllegalRequestException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<APIResponse<ErrorResponse>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.createErrResponse(HttpStatus.NOT_FOUND,
                        new ErrorResponse(
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                "해당 페이지가 존재하지 않습니다",
                                ErrorCode.NOT_FOUND_HANDLER)));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(APIResponse.createErrResponse(HttpStatus.METHOD_NOT_ALLOWED,
                        new ErrorResponse(
                                HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                                "해당 메소드를 지원하지 않습니다. 파라미터를 확인해주세요.",
                                ErrorCode.NOT_SUPPORTED_METHOD)));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(APIResponse.createErrResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        new ErrorResponse(
                                HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
                                "요청 media type이 맞지 않습니다.",
                                ErrorCode.NOT_SUPPORTED_MEDIA_TYPE)));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> internalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "request JSON 파싱 에러. request body를 확인해주세요.",
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler(UuidValidException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> uuidValidException(UuidValidException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.createErrResponse(HttpStatus.BAD_REQUEST,
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "요청 id 에러. " + e.getMessage(),
                                ErrorCode.INVALID_REQUEST)));
    }

    @ExceptionHandler(JwtTokenPayloadException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> jwtTokenPayloadException(JwtTokenPayloadException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.createErrResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.FAIL_TOKEN_PARSING)));
    }

    @ExceptionHandler(GenericJDBCException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> GenericJDBCExceptionException(GenericJDBCException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.createErrResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "내부 쿼리 수행 에러. 관리자 문의",
                                ErrorCode.FAIL_QUERY)));
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> idNotFoundException(IdNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.createErrResponse(HttpStatus.NOT_FOUND,
                        new ErrorResponse(
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.NOT_FOUND_USER)));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> forbiddenException(ForbiddenException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(APIResponse.createErrResponse(HttpStatus.FORBIDDEN,
                        new ErrorResponse(
                                HttpStatus.FORBIDDEN.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.FORBIDDEN_ERROR)));
    }

    @ExceptionHandler(DuplicatedResourceException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> duplicatedIdException(DuplicatedResourceException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(APIResponse.createErrResponse(HttpStatus.CONFLICT,
                        new ErrorResponse(
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.DUPLICATED_REQUEST)));
    }

    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<APIResponse<ErrorResponse>> S3ArgumentException(AmazonS3Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.createErrResponse(HttpStatus.NOT_FOUND,
                        new ErrorResponse(
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.NOT_FOUND_RESOURCE)));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> resourceNotFoundException(ResourceNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.createErrResponse(HttpStatus.NOT_FOUND,
                        new ErrorResponse(
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.NOT_FOUND_RESOURCE)));
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> expiredTokenException(ExpiredTokenException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(APIResponse.createErrResponse(HttpStatus.UNAUTHORIZED,
                        new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.UNAUTHORIZED_ERROR)));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> mismatchException(BadCredentialsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(APIResponse.createErrResponse(HttpStatus.UNAUTHORIZED,
                        new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                e.getMessage(),
                                ErrorCode.UNAUTHORIZED_ERROR)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<ErrorResponse>> allException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.createErrResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "서버 에러. 관리자 문의",
                                ErrorCode.FAIL)));
    }
}
