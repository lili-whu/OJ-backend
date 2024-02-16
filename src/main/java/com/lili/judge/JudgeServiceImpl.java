package com.lili.judge;

import cn.hutool.json.JSONUtil;
import com.lili.constant.enums.CodeSandboxStatusEnum;
import com.lili.constant.enums.ErrorCode;
import com.lili.constant.enums.JudgeInfoMessage;
import com.lili.constant.enums.RecordSubmitStatusEnum;
import com.lili.exception.BusinessException;
import com.lili.judge.codeSandbox.CodeSandBox;
import com.lili.judge.codeSandbox.CodeSandBoxFactory;
import com.lili.judge.codeSandbox.CodeSandBoxProxy;
import com.lili.judge.codeSandbox.model.ExecuteCodeRequest;
import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import com.lili.judge.strategy.JudgeContext;
import com.lili.model.Question;
import com.lili.model.RecordSubmit;
import com.lili.model.request.question.JudgeCase;
import com.lili.judge.codeSandbox.model.JudgeInfo;
import com.lili.service.QuestionService;
import com.lili.service.RecordSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class JudgeServiceImpl implements JudgeService{

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RecordSubmitService recordSubmitService;

    @Autowired
    private JudgeManager judgeManager;

    @Value("${codeSandbox.type}")
    private String sandboxType;

    @Override
    public void doJudge(long recordSubmitId){


        // 1. 用户限流 todo 使用Redis限流用户10秒内只允许一次提交

        // 2. 获取提交记录
        RecordSubmit recordSubmit = recordSubmitService.getById(recordSubmitId);
        if(recordSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND, "提交信息不存在");
        }
        // 3. 获取问题信息
        Long questionId = recordSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交的题目不存在");
        }


        // 4. 调用代码沙箱
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(sandboxType);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        Integer language = recordSubmit.getLanguage();
        String code = recordSubmit.getCode();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).toList();
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();

        // 5. 获取执行结果
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);


        // 6.执行判题服务
        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        JudgeContext judgeContext = new JudgeContext();

        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setRecordSubmit(recordSubmit);
        judgeContext.setExecuteCodeResponse(executeCodeResponse);

        // 直接调用judgeMapper, 类似静态代理模式, 会在judgeManager中完成策略模式的选择
        JudgeInfo judgeInfoResponse = judgeManager.doJudge(judgeContext);

        // 7.结果写入数据库
        RecordSubmit recordSubmitUpdate = new RecordSubmit();
        recordSubmitUpdate.setId(recordSubmitId);
        recordSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoResponse));
        if(JudgeInfoMessage.SYSTEM_ERROR.getCode().equals(Objects.requireNonNull(JudgeInfoMessage.getEnumByValue(judgeInfoResponse.getMessage())).getCode())){
            recordSubmitUpdate.setStatus(RecordSubmitStatusEnum.FAILED.getStatus());
        }else{
            // 不是系统错误都可以认为是成功
            recordSubmitUpdate.setStatus(RecordSubmitStatusEnum.SUCCESS.getStatus());
        }
        recordSubmitUpdate.setResult(Objects.requireNonNull(JudgeInfoMessage.getEnumByValue(judgeInfoResponse.getMessage())).getCode());
        boolean result = recordSubmitService.updateById(recordSubmitUpdate);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
    }
}
