package com.lili.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lili.constant.SortConstant;
import com.lili.constant.StringConstant;
import com.lili.constant.enums.ErrorCode;
import com.lili.constant.enums.RecordSubmitStatusEnum;
import com.lili.constant.enums.UserRole;
import com.lili.exception.BusinessException;
import com.lili.judge.JudgeService;
import com.lili.mapper.RecordSubmitMapper;
import com.lili.model.PageResult;
import com.lili.model.Question;
import com.lili.model.RecordSubmit;
import com.lili.judge.codeSandbox.model.JudgeInfo;
import com.lili.model.request.recordSubmit.RecordSubmitAddRequest;
import com.lili.model.request.recordSubmit.RecordSubmitQueryRequest;
import com.lili.model.vo.recordSubmit.RecordSubmitVO;
import com.lili.model.vo.user.SafetyUser;
import com.lili.service.QuestionService;
import com.lili.service.RecordSubmitService;
import com.lili.service.UserService;
import com.lili.utils.SubmissionLimit;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Autowired
    @Lazy
    private JudgeService judgeService;
    @Autowired
    private RecordSubmitMapper recordSubmitMapper;

    @Resource
    private SubmissionLimit submissionLimit;
    /**
     * 题目提交
     *
     * @param recordSubmitAddRequest
     * @param httpServletRequest
     * @return 提交记录的id
     */
    @Override
    public Long doRecordSubmit(RecordSubmitAddRequest recordSubmitAddRequest, HttpServletRequest httpServletRequest){
        // 首先使用redis 做限流, 10秒内只允许用户一次提交
        if(!submissionLimit.trySubmit(httpServletRequest)){
            throw new BusinessException(ErrorCode.SUBMIT_TOO_MUCH, "提交速度太快, 请等待后重试");
        }
        // 判断题目是否存在
        long questionId = recordSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }

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
        CompletableFuture.runAsync(() -> judgeService.doJudge(recordSubmit.getId()));
        return recordSubmit.getId();
    }


    /**
     * 拼接查询条件和排序条件
     * @param recordSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<RecordSubmit> getQueryWrapper(RecordSubmitQueryRequest recordSubmitQueryRequest){
        Integer language = recordSubmitQueryRequest.getLanguage();
        Long questionId = recordSubmitQueryRequest.getQuestionId();
        Long createId = recordSubmitQueryRequest.getCreateId();
        Integer result = recordSubmitQueryRequest.getResult();
        String sortField = recordSubmitQueryRequest.getSortField();
        String sortOrder = recordSubmitQueryRequest.getSortOrder();

        QueryWrapper<RecordSubmit> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(ObjectUtil.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtil.isNotEmpty(questionId), "question_id", questionId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(createId), "create_id", createId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(result), "result", result);

        // 检查排序字段是否合法, 看排序字段是不是recordSubmit中的属性
        boolean valid = false;
        for(Field field: RecordSubmit.class.getDeclaredFields()){
            if (field.getName().equals(sortField)) {
                valid = true;
                break;
            }
        };
        queryWrapper.orderBy(valid, SortConstant.SORT_ORDER_ASC.equals(sortOrder), StringUtils.camelToUnderline(sortField));

        return queryWrapper;
    }

    @Override
    public PageResult<RecordSubmitVO> getRecordSubmitPageVO(RecordSubmitQueryRequest recordSubmitQueryRequest, HttpServletRequest httpServletRequest){
        long current = recordSubmitQueryRequest.getCurrent();
        long size = recordSubmitQueryRequest.getPageSize();
        IPage<RecordSubmit> iPage = new Page<>();
        iPage.setSize(size);
        iPage.setCurrent(current);
        // 得到调用方法的user
        SafetyUser user = userService.getLoginUser(httpServletRequest);
        // 脱敏转换为VO返回
        List<RecordSubmitVO> recordSubmitVOS = recordSubmitMapper.selectList(iPage, this.getQueryWrapper(recordSubmitQueryRequest))
                .stream().map(recordSubmit -> getRecordSubmitVO(recordSubmit, user)).toList();
        return new PageResult<>(iPage.getTotal(), recordSubmitVOS);

    }

    @Override
    public RecordSubmitVO getRecordSubmitVO(RecordSubmit recordSubmit, SafetyUser user){
        RecordSubmitVO recordSubmitVO = new RecordSubmitVO();
        BeanUtils.copyProperties(recordSubmit, recordSubmitVO);
        // 如果非管理员或者用户本人查询, 则不返回答案
        if(recordSubmit.getCreateId() != user.getId() || user.getUserRole() != UserRole.ADMIN_ROLE.getRole()){
            recordSubmit.setCode("******");
        }
        // 转换JudgeInfo
        recordSubmitVO.setJudgeInfo(JSONUtil.toBean(recordSubmit.getJudgeInfo(), JudgeInfo.class));
        return recordSubmitVO;

    }

    @Override
    public RecordSubmitVO getSingleRecordSubmit(Long id,  HttpServletRequest request){
        // 得到调用方法的user
        SafetyUser user = userService.getLoginUser(request);
        // 脱敏转换为VO返回
        RecordSubmit recordSubmit = recordSubmitMapper.selectById(id);
        return getRecordSubmitVO(recordSubmit, user);
    }
}




