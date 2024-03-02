package com.rayzhou.usercenter.exception;

import com.rayzhou.usercenter.common.BaseResponse;
import com.rayzhou.usercenter.common.ErrorCode;
import com.rayzhou.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        return ResultUtils.createFailureResponse(e.getCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("System caught a unknown failure: ", e);
        return ResultUtils.createFailureResponse(ErrorCode.INPUT_ERROR);
    }
}
