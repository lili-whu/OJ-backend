package com.lili.judge.codeSandbox;

import com.lili.judge.codeSandbox.impl.ExampleCodeSandbox;
import com.lili.judge.codeSandbox.impl.RemoteCodeSandbox;
import com.lili.judge.codeSandbox.impl.ThirdPartyCodeSandbox;

public class CodeSandBoxFactory{
    /**
     * 静态工厂创建代码沙箱示例
     */

    public static CodeSandBox newInstance(String type){
        switch(type){
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
