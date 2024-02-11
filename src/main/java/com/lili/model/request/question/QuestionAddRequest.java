package com.lili.model.request.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class QuestionAddRequest implements Serializable {

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


    private String difficulty;
    /**
     * 测试用例, json数组
     */
    private List<JudgeCase> judgeCase;

    /**
     * 时空条件限制
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;
}