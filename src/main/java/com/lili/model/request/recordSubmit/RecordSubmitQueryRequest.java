package com.lili.model.request.recordSubmit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lili.model.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecordSubmitQueryRequest extends PageRequest implements Serializable{
    /**
     * 使用语言, 枚举值
     */
    private Integer language;

    /**
     * 判题结果(属于JudgeInfo)
     */
    private Integer resultStatus;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 提交的用户id
     */
    private Long createId;

    private static final long serialVersionUID = 1L;
}
