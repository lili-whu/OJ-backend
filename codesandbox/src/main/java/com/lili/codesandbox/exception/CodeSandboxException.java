package com.lili.codesandbox.exception;


import lombok.Getter;

@Getter
public class CodeSandboxException extends RuntimeException{
    // 沙箱执行状态
    private final Integer status;

    public CodeSandboxException(String message, Integer status){
        super(message);
        this.status = status;
    }
}
