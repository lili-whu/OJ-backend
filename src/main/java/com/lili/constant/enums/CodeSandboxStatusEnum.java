package com.lili.constant.enums;

public enum CodeSandboxStatusEnum{
    CORRECT(1, "正常执行"),
    SYSTEM_ERROR(2, "系统错误"),
    COMPILE_ERROR(3, "编译错误"),
    RUNTIME_ERROR(4, "执行错误"),

    TIME_LIMIT_ERROR(5, "执行超时"),
    MEMORY_ERROR(6, "内存溢出");
    ;

    CodeSandboxStatusEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    private Integer code;
    private String message;

    public Integer getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
