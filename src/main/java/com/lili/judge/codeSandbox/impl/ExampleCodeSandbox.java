package com.lili.judge.codeSandbox.impl;

import com.lili.constant.enums.JudgeInfoMessage;
import com.lili.constant.enums.RecordSubmitStatusEnum;
import com.lili.judge.codeSandbox.CodeSandBox;
import com.lili.judge.codeSandbox.model.ExecuteCodeRequest;
import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import com.lili.judge.codeSandbox.model.JudgeInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExampleCodeSandbox implements CodeSandBox{
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        List<String> inputList = executeCodeRequest.getInputList();


        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("示例执行成功");
        executeCodeResponse.setStatus(RecordSubmitStatusEnum.SUCCESS.getStatus());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessage.ACCEPTED.getValue());
        judgeInfo.setMemoryConsume(100L);
        judgeInfo.setTimeConsume(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
