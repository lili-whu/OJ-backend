package com.lili.model.request.question;

import com.lili.model.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String title;


    private String difficulty;

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
     * 创建题目的用户id
     */
    private Long createId;

    private static final long serialVersionUID = 1L;
}