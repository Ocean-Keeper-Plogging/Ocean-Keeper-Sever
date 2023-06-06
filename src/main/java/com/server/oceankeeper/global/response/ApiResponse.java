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

    // 예외 발생으로 API 호출 실패시 반환
    public static ApiResponse createError(String message) {
        return new ApiResponse(ErrorCode.FAIL, message);
    }

    private ApiResponse(ErrorCode status, String message) {
        this.status = status;
        this.message = message;
    }
}