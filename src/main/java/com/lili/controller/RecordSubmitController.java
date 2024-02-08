package com.lili.controller;

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

//    @PostMapping("/")
//    public BaseResponse<Integer> doThumb(@RequestBody RecordSubmitAddRequest RecordSubmitAddRequest,
//                                         HttpServletRequest request) {
//        if (RecordSubmitAddRequest == null || RecordSubmitAddRequest.getPostId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // 登录才能点赞
//        final SafetyUser loginUser = userService.getLoginUser(request);
//        long postId = RecordSubmitAddRequest.getPostId();
//        int result = RecordSubmitService.doRecordSubmit(postId, loginUser);
//        return ResultUtils.success(result);
//    }

}
