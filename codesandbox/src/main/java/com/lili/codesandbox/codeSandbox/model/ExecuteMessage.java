package com.lili.codesandbox.codeSandbox.model;

import lombok.Data;

/**
 * 命令行执行信息
 */

@Data
public class ExecuteMessage{
    private Integer exitValue;
    private String message;
    private String errorMessage;
    private Long time;
}
