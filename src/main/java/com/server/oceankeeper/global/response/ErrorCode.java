package com.server.oceankeeper.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    FAIL(9999, "실패"),
    NOT_SUPPORTED_MEDIA_TYPE(997, "지원하지 않는 미디어 타입"),
    NOT_SUPPORTED_METHOD(998, "지원하지 않는 메소드"),
    NOT_FOUND_RESOURCE(999, "요청한 자원이 존재하지 않음"),
    NOT_FOUND_USER(1000, "NOT FOUND USER"),
    NOT_FOUND_TOKEN(1001, "NOT FOUND TOKEN"),
    NOT_FOUND_HANDLER(1002, "NOT FOUND HANDLER"),
    FAIL_TOKEN_PARSING(1003, "FAIL TOKEN PARSING"),
    FAIL_QUERY(1004, "쿼리 에러"),
    UNAUTHORIZED_ERROR(1005, "허가되지 않은 요청"),
    FORBIDDEN_ERROR(1006, "허가되지 않은 요청"),
    DUPLICATED_REQUEST(1007, "중복된 요청"),

    SUCCESS(200, "SUCCESS"),
    INVALID_REQUEST(400, "올바르지 않은 요청");

    private final int code;
    private final String message;
}
