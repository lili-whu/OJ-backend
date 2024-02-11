package com.lili.judge.codeSandbox.impl;

import com.lili.constant.enums.LanguageEnum;
import com.lili.judge.codeSandbox.CodeSandBox;
import com.lili.judge.codeSandbox.model.ExecuteCodeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExampleCodeSandboxTest{
    @Test
    void exeCode(){
        CodeSandBox codeSandBox = new ExampleCodeSandbox();
        String code = "int main() { System.out.println(1)}";
        Integer language = LanguageEnum.JAVA.getCode();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        codeSandBox.executeCode(executeCodeRequest);

    }
}