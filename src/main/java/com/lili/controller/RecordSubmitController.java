package com.lili.controller;

import com.lili.annotation.UserRoleAnnotation;
import com.lili.constant.enums.UserRole;
import com.lili.model.PageResult;
import com.lili.model.Result;
import com.lili.model.request.question.QuestionQueryRequest;
import com.lili.model.request.recordSubmit.RecordSubmitAddRequest;
import com.lili.model.request.recordSubmit.RecordSubmitQueryRequest;
import com.lili.model.vo.question.QuestionAdminVO;
import com.lili.model.vo.question.QuestionUserVO;
import com.lili.model.vo.recordSubmit.RecordSubmitVO;
import com.lili.service.RecordSubmitService;
import com.lili.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/record")
@Slf4j
public class RecordSubmitController {

    @Autowired
    private RecordSubmitService recordSubmitService;

    @Autowired
    private UserService userService;


    @PostMapping("/")
    @UserRoleAnnotation(UserRole.DEFAULT_ROLE)
    public Result<Long>recordSubmit(@RequestBody RecordSubmitAddRequest recordSubmitAddRequest,
                                   HttpServletRequest httpServletRequest) {
        Long id = recordSubmitService.doRecordSubmit(recordSubmitAddRequest, httpServletRequest);
        return Result.success(id);
    }

    /**
     * 分页获取提交记录列表, 只有用户自己或者管理员可以查看自己的answer
     * 和questionQuery先判断用户身份不同, 这里是在service层做校验
     * @param recordSubmitQueryRequest
     * @return
     */
    @PostMapping("/page")
    @UserRoleAnnotation(UserRole.DEFAULT_ROLE)
    public Result<PageResult<RecordSubmitVO>> getRecordSubmitPage(@RequestBody RecordSubmitQueryRequest recordSubmitQueryRequest, HttpServletRequest request) {
        PageResult<RecordSubmitVO> recordSubmitPageVO = recordSubmitService.getRecordSubmitPageVO(recordSubmitQueryRequest, request);

        return Result.success(recordSubmitPageVO);
    }

}
