package com.lili.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lili.mapper.QuestionMapper;
import com.lili.model.Question;
import com.lili.service.QuestionService;
import org.springframework.stereotype.Service;

/**
* @author lili
* @description 针对表【question】的数据库操作Service实现
* @createDate 2024-02-07 23:31:48
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




