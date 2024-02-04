package com.lili.exception;

import com.lili.constant.enums.ErrorCode;

/**
 * 自定义异常类, 出现问题时返回自定义异常
 */
public class BusinessException extends RuntimeException{
    private final int code;

    /**
     *
     * @param code 错误码
     * @param message 继承RuntimeException的message字段
     */
    public BusinessException(int code, String message){
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode, String message){
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode(){
        return code;
    }
}
