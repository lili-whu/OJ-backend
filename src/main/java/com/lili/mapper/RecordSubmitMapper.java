package com.lili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lili.model.RecordSubmit;

import java.util.List;


/**
* @author lili
* @description 针对表【record_submit】的数据库操作Mapper
* @createDate 2024-02-07 23:28:01
* @Entity generator.domain.RecordSubmit
*/
public interface RecordSubmitMapper extends BaseMapper<RecordSubmit> {

    List<RecordSubmit> selectByQuestionIdList(List<Long> questionIdList, Long userId);
}




