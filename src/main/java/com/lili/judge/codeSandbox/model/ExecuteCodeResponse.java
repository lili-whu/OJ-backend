package com.lili.judge.codeSandbox.model;

import com.lili.model.request.recordSubmit.JudgeInfo;
import lombok.Data;

import java.util.List;

@Data
public class ExecuteCodeResponse{
    private List<String> outputList;
    /**
     * 接口信息
     */
    private String message;
    /**
     * 程序执行状态
     */
    private Integer status;
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
