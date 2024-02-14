package com.lili.model.vo.recordSubmit;

import com.lili.judge.codeSandbox.model.JudgeInfo;
import com.lili.model.vo.question.QuestionUserVO;
import com.lili.model.vo.user.SafetyUser;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查询提交记录接口
 */
@Data
public class RecordSubmitVO implements Serializable{
    /**
     * id
     */
    private Long id;

    /**
     * 使用语言, 枚举值
     */
    private Integer language;

    /**
     * 代码
     */
    private String code;

    /**
     * 判题信息(错误类型, 时间消耗, 空间消耗)
     */
    private JudgeInfo judgeInfo;

    /**
     * 提交状态(0 未判题 1 判题中 2 成功 3 失败)
     */
    private Integer status;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 提交的用户id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 提交的用户信息
     */
    private SafetyUser safetyUser;

    /**
     * 题目信息
     */
    private QuestionUserVO questionUserVO;


    private static final long serialVersionUID = 1L;
}
