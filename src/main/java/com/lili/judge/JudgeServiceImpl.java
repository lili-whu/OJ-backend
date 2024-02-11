package com.lili.judge;

import cn.hutool.json.JSONUtil;
import com.lili.constant.enums.ErrorCode;
import com.lili.constant.enums.JudgeInfoMessage;
import com.lili.constant.enums.LanguageEnum;
import com.lili.constant.enums.RecordSubmitStatusEnum;
import com.lili.exception.BusinessException;
import com.lili.judge.codeSandbox.CodeSandBox;
import com.lili.judge.codeSandbox.CodeSandBoxFactory;
import com.lili.judge.codeSandbox.CodeSandBoxProxy;
import com.lili.judge.codeSandbox.model.ExecuteCodeRequest;
import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import com.lili.judge.strategy.DefaultStrategy;
import com.lili.judge.strategy.JudgeContext;
import com.lili.judge.strategy.JudgeStrategy;
import com.lili.model.Question;
import com.lili.model.RecordSubmit;
import com.lili.model.request.question.JudgeCase;
import com.lili.model.request.question.JudgeConfig;
import com.lili.model.request.recordSubmit.JudgeInfo;
import com.lili.model.vo.recordSubmit.RecordSubmitVO;
import com.lili.service.QuestionService;
import com.lili.service.RecordSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

        // 1. 获取提交记录
        RecordSubmit recordSubmit = recordSubmitService.getById(recordSubmitId);
        if(recordSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND, "提交信息不存在");
        }
        // 2. 获取问题信息
        Long questionId = recordSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交的题目不存在");
        }
        // 3. 判断题目状态是否为等待 todo 用户在题目判题结束前只允许一次提交, 不允许一直提交相同的内容
        if(!recordSubmit.getStatus().equals(RecordSubmitStatusEnum.WAITING.getStatus())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目已经在判题中");
        }
        //4. 调用代码沙箱, 获取执行结果
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
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);

        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        JudgeContext judgeContext = new JudgeContext();

        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setRecordSubmit(recordSubmit);
        // 直接调用judgeMapper, 类似静态代理模式, 会在judgeManager中完成策略模式的选择
        JudgeInfo judgeInfoResponse = judgeManager.doJudge(judgeContext);
        // todo 修改数据库中的判题结果
        RecordSubmit recordSubmitUpdate = new RecordSubmit();
        recordSubmitUpdate.setId(recordSubmitId);
        recordSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoResponse));
        recordSubmitUpdate.setStatus(RecordSubmitStatusEnum.SUCCESS.getStatus());
        boolean result = recordSubmitService.updateById(recordSubmitUpdate);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
    }
}
