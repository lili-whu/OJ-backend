package com.lili.service.impl;

import com.lili.service.QuestionService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionServiceImplTest{
    @Resource
    private QuestionService questionService;
    @Test
    public void testGetQuestionByUser(){
    };
}