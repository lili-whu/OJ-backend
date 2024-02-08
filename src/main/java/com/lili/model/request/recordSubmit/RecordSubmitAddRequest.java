package com.lili.model.request.recordSubmit;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecordSubmitAddRequest implements Serializable{
    /**
     * 使用语言, 枚举值
     */
    private Integer language;

    /**
     * 代码
     */
    private String code;

    /**
     * 题目id
     */
    private Long questionId;


    private static final long serialVersionUID = 1L;
}
