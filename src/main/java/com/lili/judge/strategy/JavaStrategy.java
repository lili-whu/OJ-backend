package com.lili.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.lili.constant.enums.JudgeInfoMessage;
import com.lili.model.Question;
import com.lili.model.request.question.JudgeCase;
import com.lili.model.request.question.JudgeConfig;
import com.lili.model.request.recordSubmit.JudgeInfo;

import java.util.List;

public class JavaStrategy implements JudgeStrategy{
    // Java多10秒执行时间
    long JAVA_TIME = 10000L;
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext){
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();

        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemoryConsume(judgeInfo.getMemoryConsume());
        judgeInfoResponse.setTimeConsume(judgeInfo.getTimeConsume());
        // 5. 根据执行结果, 判断是否正确
        JudgeInfoMessage judgeInfoMessage = null;

        //5.1. 判断输入输出数量是否相等
        if(outputList.size() != inputList.size()){
            judgeInfoMessage = JudgeInfoMessage.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;
        }
        //5.2. 判断是否有不符合答案的情况
        for(int i = 0; i < judgeCaseList.size(); i++){
            JudgeCase judgeCase = judgeCaseList.get(i);
            if(judgeCase.getOutput().equals(outputList.get(i))){
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
        judgeInfoMessage = JudgeInfoMessage.ACCEPTED;
        judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
        return judgeInfoResponse;



    }
}
