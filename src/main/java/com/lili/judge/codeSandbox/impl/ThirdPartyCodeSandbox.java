package com.lili.judge.codeSandbox.impl;

import com.lili.judge.codeSandbox.CodeSandBox;
import com.lili.judge.codeSandbox.model.ExecuteCodeRequest;
import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyCodeSandbox implements CodeSandBox{
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        return null;
    }
}
