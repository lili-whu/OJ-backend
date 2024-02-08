package com.lili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lili.model.Question;
import com.lili.model.request.question.QuestionAddRequest;
import com.lili.model.request.question.QuestionQueryRequest;
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
    public void validQuestion(Question question);

    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    List<QuestionUserVO> getQuestionsByUser(QuestionQueryRequest questionQueryRequest);

    List<QuestionAdminVO> getQuestionsByAdmin(QuestionQueryRequest questionQueryRequest);



    QuestionUserVO getQuestionUserVO(Question question);

    QuestionAdminVO getQuestionAdminVO(Question question);

    long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request);
}
