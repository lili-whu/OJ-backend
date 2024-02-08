package com.lili.controller;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lili.annotation.UserRoleAnnotation;
import com.lili.constant.enums.ErrorCode;
import com.lili.constant.enums.UserRole;
import com.lili.exception.BusinessException;
import com.lili.model.Question;
import com.lili.model.Result;
import com.lili.model.User;
import com.lili.model.request.question.QuestionAddRequest;
import com.lili.model.request.question.QuestionQueryRequest;
import com.lili.model.request.question.QuestionUpdateRequest;
import com.lili.model.vo.SafetyUser;
import com.lili.model.vo.question.QuestionAdminVO;
import com.lili.model.vo.question.QuestionUserVO;
import com.lili.service.QuestionService;
import com.lili.service.UserService;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        long newQuestionId = questionService.addQuestion(questionAddRequest, request);
        return Result.success(newQuestionId);
    }

    /**
     * 删除(管理员方法)
     *
     * @param request
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @UserRoleAnnotation(UserRole.ADMIN_ROLE)
    public Result<Boolean> deleteQuestion(@PathVariable Long id, HttpServletRequest request) {
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if(oldQuestion == null){
            throw new BusinessException(ErrorCode.NOT_FOUND, "删除的题目已经不存在");
        }
        boolean b = questionService.removeById(id);
        return Result.success(b);
    }

    /**
     * 更新(管理员方法)
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @UserRoleAnnotation(UserRole.ADMIN_ROLE)
    public Result<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        question.setJudgeConfig(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeConfig()));
        question.setJudgeCase(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeCase()));
        // 参数校验
        questionService.validQuestion(question);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if(oldQuestion == null){
            throw new BusinessException(ErrorCode.NOT_FOUND, "需要更新的题目不存在");
        }
        boolean result = questionService.updateById(question);
        return Result.success(result);
    }

    /**
     * 根据 id 获取(user)
     *
     * @param id
     * @return
     */
    @GetMapping("user/get/{id}")
    public Result<QuestionUserVO> getUserQuestionById(@PathVariable long id, HttpServletRequest request) {
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到该题目");
        }
        return Result.success(questionService.getQuestionUserVO(question));
    }

    /**
     * 根据 id 获取(admin)
     *
     * @param id
     * @return
     */
    @GetMapping("admin/get/{id}")
    @UserRoleAnnotation(UserRole.ADMIN_ROLE)
    public Result<QuestionAdminVO> getAdminQuestionById(@PathVariable long id, HttpServletRequest request) {
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到该题目");
        }
        return Result.success(questionService.getQuestionAdminVO(question));
    }

    /**
     * 分页获取题目列表(管理员)
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/admin/page")
    @UserRoleAnnotation(UserRole.ADMIN_ROLE)
    public Result<List<QuestionAdminVO>> getQuestionsByAdmin(@RequestBody QuestionQueryRequest questionQueryRequest) {
        List<QuestionAdminVO> questions = questionService.getQuestionsByAdmin(questionQueryRequest);

        return Result.success(questions);
    }

    /**
     * 分页获取题目列表(用户)
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/user/page")
    public Result<List<QuestionUserVO>> getQuestionsByUser(@RequestBody QuestionQueryRequest questionQueryRequest) {
        List<QuestionUserVO> questions = questionService.getQuestionsByUser(questionQueryRequest);
        return Result.success(questions);
    }
}
