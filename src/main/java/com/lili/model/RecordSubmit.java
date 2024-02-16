package com.lili.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * @TableName record_submit
 */
@TableName(value ="record_submit")
@Data
public class RecordSubmit implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
    private String judgeInfo;

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
     * 提交结果
     */
    private int result;

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