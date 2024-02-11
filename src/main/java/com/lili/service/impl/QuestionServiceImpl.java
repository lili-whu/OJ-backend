package com.lili.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lili.constant.SortConstant;
import com.lili.constant.enums.ErrorCode;
import com.lili.exception.BusinessException;
import com.lili.mapper.QuestionMapper;
import com.lili.model.PageResult;
import com.lili.model.Question;
import com.lili.model.request.question.*;
import com.lili.model.vo.user.SafetyUser;
import com.lili.model.vo.question.QuestionAdminVO;
import com.lili.model.vo.question.QuestionUserVO;
import com.lili.service.QuestionService;
import com.lili.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

/**
* @author lili
* @description 针对表【question】的数据库操作Service实现
* @createDate 2024-02-07 23:31:48
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserService userService;
    /**
     * 校验新建的题目是否合法, 判断为空或null,以及合法性, 校验应该对原始而非数据库类型校验
     * @param request 问题DTO
     */
    @Override
    public void validQuestionRequest(QuestionAddRequest request){
        if(StringUtils.isBlank(request.getTitle()) || request.getTitle().length() > 100){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目为空或过长");
        }
        if(StringUtils.isBlank(request.getDescription()) || request.getDescription().length() > 8192){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容为空或内容过长");
        }
        if(ObjectUtil.isEmpty(request.getTags())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签为空");
        }
        if(StringUtils.isEmpty(request.getDifficulty())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "难度为空");
        }
        if(request.getAnswer() != null && request.getAnswer().length() > 8192){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if(ObjectUtil.isEmpty(request.getJudgeCase())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "测试用例为空");
        }
        if(ObjectUtil.isEmpty(request.getJudgeConfig())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "测试条件为空");
        }

    }


    /**
     * 拼接查询条件和排序条件
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest){
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String description = questionQueryRequest.getDescription();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long createId = questionQueryRequest.getCreateId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        String difficulty = questionQueryRequest.getDifficulty();

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        queryWrapper.eq(StringUtils.isNotBlank(difficulty), "difficulty", difficulty);

        // tags查询条件, 单独tag作为查询条件
        if(tags != null && !tags.isEmpty()){
            for(String tag: tags){
                queryWrapper.like("tags", tag);
            }
        }

        queryWrapper.eq(ObjectUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtil.isNotEmpty(createId), "create_id", createId);

        // 检查排序字段是否合法, 看排序字段是不是question中的属性
        // getDeclaredField得到私有字段
        boolean valid = false;
        for(Field field: Question.class.getDeclaredFields()){
            if (field.getName().equals(sortField)) {
                valid = true;
                break;
            }
        };
        queryWrapper.orderBy(valid, SortConstant.SORT_ORDER_ASC.equals(sortOrder), StringUtils.camelToUnderline(sortField));

        return queryWrapper;
    }

    @Override
    public PageResult<QuestionUserVO> getQuestionsByUser(QuestionQueryRequest questionQueryRequest){
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        IPage<Question> iPage = new Page<>();
        iPage.setSize(size);
        iPage.setCurrent(current);
        List<QuestionUserVO> questions = questionMapper.selectList(iPage, this.getQueryWrapper(questionQueryRequest))
                .stream().map(this::getQuestionUserVO).toList();
        return new PageResult<QuestionUserVO>(iPage.getTotal(), questions);

    }

    @Override
    public PageResult<QuestionAdminVO> getQuestionsByAdmin(QuestionQueryRequest questionQueryRequest){
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        IPage<Question> iPage = new Page<>();
        iPage.setSize(size);
        iPage.setCurrent(current);

        List<QuestionAdminVO> questionAdminVOS = questionMapper.selectList(iPage, this.getQueryWrapper(questionQueryRequest))
                .stream().map(this::getQuestionAdminVO).toList();
        return new PageResult<QuestionAdminVO>(iPage.getTotal(), questionAdminVOS);
    }

    @Override
    public QuestionUserVO getQuestionUserVO(Question question){
        QuestionUserVO questionUserVO = new QuestionUserVO();
        BeanUtils.copyProperties(question, questionUserVO);
        questionUserVO.setJudgeConfig(JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class));
        questionUserVO.setTags(JSONUtil.toList(question.getTags(), String.class));
        return questionUserVO;
    }

    @Override
    public QuestionAdminVO getQuestionAdminVO(Question question){
        QuestionAdminVO questionAdminVO = new QuestionAdminVO();
        BeanUtils.copyProperties(question, questionAdminVO);
        questionAdminVO.setJudgeConfig(JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class));
        questionAdminVO.setTags(JSONUtil.toList(question.getTags(), String.class));
        questionAdminVO.setJudgeCase(JSONUtil.toList(question.getJudgeCase(), JudgeCase.class));
        return questionAdminVO;
    }

    /**
     * 添加题目
     * @param questionAddRequest
     * @param request
     * @return
     */
    @Override
    public long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request){
        this.validQuestionRequest(questionAddRequest);
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        // tags转化为String
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        question.setJudgeConfig(JSONUtil.toJsonStr(questionAddRequest.getJudgeConfig()));
        question.setJudgeCase(JSONUtil.toJsonStr(questionAddRequest.getJudgeCase()));
        // 校验题目字段

        // 加入创建人id
        SafetyUser loginUser = userService.getLoginUser(request);
        question.setCreateId(loginUser.getId());
        // 保存到question表
        boolean result = this.save(question);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目保存失败");
        }
        return question.getId();
    }

    @Override
    public boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest){
        // 对题目参数校验
        QuestionAddRequest questionAddRequest = new QuestionAddRequest();
        BeanUtils.copyProperties(questionUpdateRequest, questionAddRequest);
        this.validQuestionRequest(questionAddRequest);
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        question.setJudgeConfig(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeConfig()));
        question.setJudgeCase(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeCase()));
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = this.getById(id);
        if(oldQuestion == null){
            throw new BusinessException(ErrorCode.NOT_FOUND, "需要更新的题目不存在");
        }
        return this.updateById(question);
    }
}


