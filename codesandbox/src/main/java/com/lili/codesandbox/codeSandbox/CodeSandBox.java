package com.lili.codesandbox.codeSandbox;


import com.lili.codesandbox.codeSandbox.model.ExecuteCodeRequest;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口, 给定输入, 沙箱作为黑盒, 得到运行结果
 */
public interface CodeSandBox{
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
