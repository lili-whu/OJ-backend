package com.lili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lili.model.RecordSubmit;
import com.lili.model.request.recordSubmit.RecordSubmitAddRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author lili
* @description 针对表【record_submit】的数据库操作Service
* @createDate 2024-02-07 23:28:01
*/
public interface RecordSubmitService extends IService<RecordSubmit> {

    Long doRecordSubmit(RecordSubmitAddRequest recordSubmitAddRequest, HttpServletRequest httpServletRequest);
}
