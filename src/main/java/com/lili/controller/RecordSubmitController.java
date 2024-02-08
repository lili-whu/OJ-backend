package com.lili.controller;

import com.lili.exception.BusinessException;
import com.lili.model.Result;
import com.lili.model.request.recordSubmit.RecordSubmitAddRequest;
import com.lili.model.vo.SafetyUser;
import com.lili.service.RecordSubmitService;
import com.lili.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/record")
@Slf4j
public class RecordSubmitController {

    @Autowired
    private RecordSubmitService recordSubmitService;

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public Result<Long>recordSubmit(@RequestBody RecordSubmitAddRequest recordSubmitAddRequest,
                                   HttpServletRequest httpServletRequest) {
        Long id = recordSubmitService.doRecordSubmit(recordSubmitAddRequest, httpServletRequest);
        return Result.success(id);
    }

}
