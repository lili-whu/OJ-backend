package com.lili.judge.codeSandbox.model;

import lombok.Data;

@Data
public class JudgeInfo{
    private String message;

    private Long memoryConsume;

    private Long timeConsume;

    private String detailMessage;
}
