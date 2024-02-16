package com.lili.judge.strategy;

import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import com.lili.model.Question;
import com.lili.model.RecordSubmit;
import com.lili.model.request.question.JudgeCase;
import com.lili.judge.codeSandbox.model.JudgeInfo;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext{


    private List<JudgeCase> judgeCaseList;

    private Question question;

    private RecordSubmit recordSubmit;

    private ExecuteCodeResponse executeCodeResponse;

}
