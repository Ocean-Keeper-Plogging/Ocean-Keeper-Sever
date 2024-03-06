package com.server.oceankeeper.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ApiHeader {
    private final int resultCode; // 성공 200, 실패다 999, 600
    private final String  codeName; // success, fail, NOT_FOUND_USER
}
