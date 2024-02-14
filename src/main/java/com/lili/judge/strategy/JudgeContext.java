package com.lili.judge.strategy;

import com.lili.model.Question;
import com.lili.model.RecordSubmit;
import com.lili.model.request.question.JudgeCase;
import com.lili.judge.codeSandbox.model.JudgeInfo;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext{

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private RecordSubmit recordSubmit;

}
