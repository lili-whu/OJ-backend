package com.lili.model.vo.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lili.model.request.question.JudgeCase;
import com.lili.model.request.question.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class QuestionAdminVO extends CreateQuestionUserInfo implements Serializable{
    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 具体题目内容
     */
    private String description;

    /**
     * 题目标签, json数组
     */
    private List<String> tags;

    /**
     * 题解 todo 扩展为一个单独的数据表
     */
    private String answer;

    /**
     * 测试用例, json数组
     */
    private List<JudgeCase> judgeCase;

    /**
     * 时空条件限制
     */
    private JudgeConfig judgeConfig;

    /**
     * 提交次数
     */
    private Integer submitNum;

    /**
     * 通过次数
     */
    private Integer acceptNum;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favorNum;

    /**
     * 创建题目的用户id
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


    private static final long serialVersionUID = 1L;
}
