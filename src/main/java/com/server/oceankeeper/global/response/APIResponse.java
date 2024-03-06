package com.server.oceankeeper.global.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Setter
public class APIResponse<T> {
    private Integer statusCode;
    private LocalDateTime timestamp;
    private T response;

    public APIResponse(HttpStatus statusCode, LocalDateTime timestamp, T response) {
        this.statusCode = statusCode.value();
        this.timestamp = timestamp;
        this.response = response;
    }

    public static <T> APIResponse<T> createGetResponse(T response) {
        return new APIResponse<T>(HttpStatus.OK, LocalDateTime.now(), response);
    }

    public static <T> APIResponse<T> createPatchResponse(T response) {
        return new APIResponse<T>(HttpStatus.OK, LocalDateTime.now(), response);
    }

    public static <T> APIResponse<T> createPostResponse(T response) {
        return new APIResponse<T>(HttpStatus.CREATED, LocalDateTime.now(), response);
    }

    public static <T> APIResponse<T> createDeleteResponse(T response) {
        return new APIResponse<T>(HttpStatus.OK, LocalDateTime.now(), response);
    }

    public static <T> APIResponse<T> createPutResponse(T response) {
        return new APIResponse<T>(HttpStatus.OK, LocalDateTime.now(), response);
    }

    //실패
    public static <T> APIResponse<T> createErrResponse(HttpStatus statusCode, T response) {
        return new APIResponse<T>(statusCode, LocalDateTime.now(), response);
    }
}