package com.lili.model.vo.question;

import com.lili.model.request.question.JudgeConfig;
import com.lili.model.vo.recordSubmit.RecordSubmitVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionUserVO extends CreateQuestionUserInfo implements Serializable{
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


    private String difficulty;
    /**
     * 题解 todo 扩展为一个单独的数据表
     */
    private String answer;

    /**
     * 时空条件限制
     */
    private JudgeConfig judgeConfig;

    /**
     * 用户此题的提交记录
     */
    private List<RecordSubmitVO> recordSubmitVOList;

    /**
     * 用户此题的提交状态
     */
    private String submitStatus;



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
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
