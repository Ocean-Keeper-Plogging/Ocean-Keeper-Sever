package com.server.oceankeeper.global.response;

import lombok.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Setter
public class ApiResponse {
    private ErrorCode status;
    private String message;

    public static ApiResponse createSuccess(String message) {
        return new ApiResponse(ErrorCode.SUCCESS, message);
    }

    public static ApiResponse createError(String message) {
        return new ApiResponse(ErrorCode.FAIL, message);
    }

    private ApiResponse(ErrorCode status, String message) {
        this.status = status;
        this.message = message;
    }
}