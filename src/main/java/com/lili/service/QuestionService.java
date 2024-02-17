package com.lili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lili.model.PageResult;
import com.lili.model.Question;
import com.lili.model.request.question.QuestionAddRequest;
import com.lili.model.request.question.QuestionQueryRequest;
import com.lili.model.request.question.QuestionUpdateRequest;
import com.lili.model.vo.question.QuestionAdminVO;
import com.lili.model.vo.question.QuestionUserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author lili
* @description 针对表【question】的数据库操作Service
* @createDate 2024-02-07 23:31:48
*/
public interface QuestionService extends IService<Question> {
    void validQuestionRequest(QuestionAddRequest questionAddRequest);

    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    PageResult<QuestionUserVO> getQuestionsByUser(QuestionQueryRequest questionQueryRequest, HttpServletRequest httpServletRequest);

    PageResult<QuestionAdminVO> getQuestionsByAdmin(QuestionQueryRequest questionQueryRequest);



    QuestionUserVO getQuestionUserVO(Question question);

    QuestionAdminVO getQuestionAdminVO(Question question);

    long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request);

    boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest);

    QuestionUserVO getUserQuestionById(long id, HttpServletRequest request);
}
