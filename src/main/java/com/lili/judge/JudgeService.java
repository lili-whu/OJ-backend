package com.lili.judge;

import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import com.lili.model.RecordSubmit;
import com.lili.model.vo.recordSubmit.RecordSubmitVO;
import org.springframework.stereotype.Service;


public interface JudgeService{
    /**
     * 判题服务
     * @param recordSubmitId
     * @return
     */
    void doJudge(long recordSubmitId);

}
