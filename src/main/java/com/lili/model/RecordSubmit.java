package com.lili.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

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
    private String judge_info;

    /**
     * 提交状态(0 未判题 1 判题中 2 成功 3 失败)
     */
    private Integer status;

    /**
     * 题目id
     */
    private Long question_id;

    /**
     * 提交的用户id
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