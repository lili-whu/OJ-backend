package com.lili.judge.strategy;

import com.lili.judge.codeSandbox.model.JudgeInfo;

public interface JudgeStrategy{
    JudgeInfo doJudge(JudgeContext judgeContext);
}
