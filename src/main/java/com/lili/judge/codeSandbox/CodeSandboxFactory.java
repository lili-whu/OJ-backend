package com.lili.judge.codeSandbox;

import com.lili.judge.codeSandbox.impl.ExampleCodeSandbox;
import com.lili.judge.codeSandbox.impl.RemoteCodeSandbox;
import com.lili.judge.codeSandbox.impl.ThirdPartyCodeSandbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class CodeSandboxFactory{
    /**
     * 静态工厂创建代码沙箱示例
     */
    private final ApplicationContext context;

    @Autowired
    public CodeSandboxFactory(ApplicationContext context) {
        this.context = context;
    }

    public CodeSandBox newInstance(String type) {
        switch (type) {
            case "example":
                return context.getBean("exampleCodeSandbox", CodeSandBox.class);
            case "remote":
                return context.getBean("remoteCodeSandbox", CodeSandBox.class);
            case "thirdParty":
                return context.getBean("thirdPartyCodeSandbox", CodeSandBox.class);
            default:
                return context.getBean("exampleCodeSandbox", CodeSandBox.class);
        }
    }
}
