package com.lili.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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


    private String difficulty;

    /**
     * 测试用例, json数组
     */
    private String judgeCase;

    /**
     * 时空条件限制
     */
    private String judgeConfig;

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

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}