package com.lili.judge;

import com.lili.judge.strategy.DefaultStrategy;
import com.lili.judge.strategy.JavaStrategy;
import com.lili.judge.strategy.JudgeContext;
import com.lili.judge.strategy.JudgeStrategy;
import com.lili.model.RecordSubmit;
import com.lili.judge.codeSandbox.model.JudgeInfo;
import org.springframework.stereotype.Service;

/**
 * 判题管理, 简化策略模式的调用
 */
@Service
public class JudgeManager{
    JudgeInfo doJudge(JudgeContext judgeContext){
        RecordSubmit recordSubmit = judgeContext.getRecordSubmit();
        Integer language = recordSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultStrategy();
        if(language == 1){
            judgeStrategy = new JavaStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
