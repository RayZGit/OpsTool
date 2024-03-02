package com.rayzhou.usercenter.common;

import lombok.*;

/**
 * Error Code for Response
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    INPUT_ERROR(40000, "Input Error"),
    INPUT_NULL(40001, "Null Input"),
    NO_AUTH(40100, "Failed Authorization"),
    NOT_LOGIN(40101, "Not Login"),
    INTERNAL_ERROR(50000, " Internal Error");


    private final int code;
    private final String message;
}
