package com.rayzhou.usercenter.common;

/**
 * 返回工具類
 */
public class ResultUtils {

    public static <T> BaseResponse<T> createSuccessResponse(T result) {
        return new BaseResponse<>(0, "success", result);
    }

    public static BaseResponse createFailureResponse(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }
}
