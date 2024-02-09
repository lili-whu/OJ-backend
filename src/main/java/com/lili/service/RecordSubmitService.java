package com.lili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lili.model.Question;
import com.lili.model.RecordSubmit;
import com.lili.model.request.question.QuestionQueryRequest;
import com.lili.model.request.recordSubmit.RecordSubmitAddRequest;
import com.lili.model.request.recordSubmit.RecordSubmitQueryRequest;
import com.lili.model.vo.question.QuestionUserVO;
import com.lili.model.vo.recordSubmit.RecordSubmitVO;
import com.lili.model.vo.user.SafetyUser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author lili
* @description 针对表【record_submit】的数据库操作Service
* @createDate 2024-02-07 23:28:01
*/
public interface RecordSubmitService extends IService<RecordSubmit> {

    Long doRecordSubmit(RecordSubmitAddRequest recordSubmitAddRequest, HttpServletRequest httpServletRequest);

    QueryWrapper<RecordSubmit> getQueryWrapper(RecordSubmitQueryRequest recordSubmitQueryRequest);

    List<RecordSubmitVO> getRecordSubmitPageVO(RecordSubmitQueryRequest recordSubmitQueryRequest, HttpServletRequest request);

    RecordSubmitVO getRecordSubmitVO(RecordSubmit recordSubmit, SafetyUser user);
}
