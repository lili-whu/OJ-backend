package com.lili.judge.codeSandbox.impl;

import com.lili.constant.enums.LanguageEnum;
import com.lili.judge.codeSandbox.CodeSandBox;
import com.lili.judge.codeSandbox.model.ExecuteCodeRequest;
import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RemoteCodeSandboxTest{
    @Test
    void exeCode(){
        CodeSandBox codeSandBox = new RemoteCodeSandbox();
        String code = """
                public class Main{
                    public static void main(String[] args){
                        int a = Integer.parseInt(args[0]);
                        int b = Integer.parseInt(args[1]);
                        System.out.println(a + b);
                    }
                }
                """;
        Integer language = LanguageEnum.JAVA.getCode();
        List<String> inputList = Arrays.asList("1 2", "3 4", "4 4", "6 6");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        System.out.println("executeCodeResponse = " + executeCodeResponse);

    }
}