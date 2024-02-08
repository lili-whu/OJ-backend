package com.lili.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lili.constant.enums.ErrorCode;
import com.lili.constant.enums.RecordSubmitStatusEnum;
import com.lili.exception.BusinessException;
import com.lili.mapper.RecordSubmitMapper;
import com.lili.model.Question;
import com.lili.model.RecordSubmit;
import com.lili.model.request.recordSubmit.RecordSubmitAddRequest;
import com.lili.service.QuestionService;
import com.lili.service.RecordSubmitService;
import com.lili.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author lili
* @description 针对表【record_submit】的数据库操作Service实现
* @createDate 2024-02-07 23:28:01
*/
@Service
public class RecordSubmitServiceImpl extends ServiceImpl<RecordSubmitMapper, RecordSubmit>
    implements RecordSubmitService{

    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;

    /**
     * 题目提交
     *
     * @param recordSubmitAddRequest
     * @param httpServletRequest
     * @return 提交记录的id
     */
    @Override
    public Long doRecordSubmit(RecordSubmitAddRequest recordSubmitAddRequest, HttpServletRequest httpServletRequest){
        // 判断题目是否存在
        long questionId = recordSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }
        // todo 判断编程语言是否合法

        long createId = userService.getLoginUser(httpServletRequest).getId();
        // 设置初始状态
        RecordSubmit recordSubmit = new RecordSubmit();
        recordSubmit.setLanguage(recordSubmitAddRequest.getLanguage());
        recordSubmit.setCode(recordSubmitAddRequest.getCode());
        recordSubmit.setJudgeInfo("{}");
        recordSubmit.setStatus(RecordSubmitStatusEnum.WAITING.getStatus());
        recordSubmit.setQuestionId(recordSubmitAddRequest.getQuestionId());
        recordSubmit.setCreateId(createId);
        boolean save = this.save(recordSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "判题提交失败");
        }
        return recordSubmit.getId();
    }
}




