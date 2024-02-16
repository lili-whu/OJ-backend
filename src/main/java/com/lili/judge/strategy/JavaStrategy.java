package com.lili.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.lili.constant.enums.CodeSandboxStatusEnum;
import com.lili.constant.enums.JudgeInfoMessage;
import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import com.lili.model.Question;
import com.lili.model.request.question.JudgeCase;
import com.lili.model.request.question.JudgeConfig;
import com.lili.judge.codeSandbox.model.JudgeInfo;

import java.util.List;
import java.util.Objects;

public class JavaStrategy implements JudgeStrategy{
    // Java多10秒执行时间
    long JAVA_TIME = 10000L;
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext){
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        ExecuteCodeResponse executeCodeResponse = judgeContext.getExecuteCodeResponse();
        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();

        // 5. 根据执行结果, 判断是否正确并返回
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        JudgeInfoMessage judgeInfoMessage = null;
        //5.0 根据返回状态判断有无问题
        Integer status = executeCodeResponse.getStatus();
        // 状态2表示系统错误
        if(Objects.equals(status, CodeSandboxStatusEnum.SYSTEM_ERROR.getCode())){
            judgeInfoResponse.setMessage(JudgeInfoMessage.SYSTEM_ERROR.getValue());
            return judgeInfoResponse;
        }else if(Objects.equals(status, CodeSandboxStatusEnum.COMPILE_ERROR.getCode())){
            judgeInfoResponse.setMessage(JudgeInfoMessage.COMPILE_ERROR.getValue());
            // 设置详细错误信息
            judgeInfoResponse.setDetailMessage(executeCodeResponse.getMessage());
            return judgeInfoResponse;
        }else if(Objects.equals(status, CodeSandboxStatusEnum.RUNTIME_ERROR.getCode())){
            judgeInfoResponse.setMessage(JudgeInfoMessage.RUNTIME_ERROR.getValue());
            judgeInfoResponse.setDetailMessage(executeCodeResponse.getMessage());
            return judgeInfoResponse;
        }else if(Objects.equals(status, CodeSandboxStatusEnum.TIME_LIMIT_ERROR.getCode())){
            judgeInfoResponse.setMessage(JudgeInfoMessage.TIME_LIMIT_EXCEEDED.getValue());
            return judgeInfoResponse;
        } else if(Objects.equals(status, CodeSandboxStatusEnum.MEMORY_ERROR.getCode())){
            judgeInfoResponse.setMessage(JudgeInfoMessage.MEMORY_LIMIT_EXCEEDED.getValue());
            return judgeInfoResponse;
        }

        //5.1. 判断输入输出数量是否相等
        if(outputList.size() != judgeCaseList.size()){
            judgeInfoMessage = JudgeInfoMessage.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;
        }
        //5.2. 判断是否有不符合答案的情况
        for(int i = 0; i < judgeCaseList.size(); i++){
            JudgeCase judgeCase = judgeCaseList.get(i);
            if(!judgeCase.getOutput().equals(outputList.get(i))){
                judgeInfoMessage = JudgeInfoMessage.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
                return judgeInfoResponse;
            }
        }
        //5.3. 判断题目限制条件
        JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();
        if(judgeInfo.getMemoryConsume() > memoryLimit){
            judgeInfoMessage = JudgeInfoMessage.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;
        }
        if(judgeInfo.getTimeConsume() - JAVA_TIME > timeLimit){
            judgeInfoMessage = JudgeInfoMessage.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMemoryConsume(judgeInfo.getMemoryConsume());
        judgeInfoResponse.setTimeConsume(judgeInfo.getTimeConsume());
        judgeInfoMessage = JudgeInfoMessage.ACCEPTED;
        judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
        return judgeInfoResponse;



    }
}
