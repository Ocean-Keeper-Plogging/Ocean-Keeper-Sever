package com.server.oceankeeper.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class ApiBody<T> {
    private final T data;
    private final T msg;
}
