package com.lili.exception;

import com.lili.constant.enums.ErrorCode;
import com.lili.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler{
    @ExceptionHandler(BusinessException.class)
    public Result businessExceptionHandler(BusinessException e){
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e){
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误");
    }

}
