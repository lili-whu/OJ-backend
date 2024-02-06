package com.lili.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
    private String tags;

    /**
     * 题解 todo 扩展为一个单独的数据表
     */
    private String answer;

    /**
     * 测试用例, json数组
     */
    private String judge_case;

    /**
     * 时空条件限制
     */
    private String judge_config;

    /**
     * 提交次数
     */
    private Integer submit_num;

    /**
     * 通过次数
     */
    private Integer accept_num;

    /**
     * 点赞数
     */
    private Integer thumb_num;

    /**
     * 收藏数
     */
    private Integer favor_num;

    /**
     * 创建题目的用户id
     */
    private Long create_id;

    /**
     * 创建时间
     */
    private LocalDateTime create_time;

    /**
     * 更新时间
     */
    private LocalDateTime update_time;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer is_delete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}