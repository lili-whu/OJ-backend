package com.lili.judge.strategy;

import com.lili.model.request.recordSubmit.JudgeInfo;

public interface JudgeStrategy{
    JudgeInfo doJudge(JudgeContext judgeContext);
}
