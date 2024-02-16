package com.lili.codesandbox.controller;


import com.lili.codesandbox.codeSandbox.DockerCodeSandboxImpl;
import com.lili.codesandbox.codeSandbox.JavaNativeCodeSandBoxImpl;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeRequest;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MainController{

    @Resource
    DockerCodeSandboxImpl dockerCodeSandbox = new DockerCodeSandboxImpl();

    @Resource
    JavaNativeCodeSandBoxImpl javaNativeCodeSandBox = new JavaNativeCodeSandBoxImpl();


    @PostMapping("/exeCodeNative")
    public ExecuteCodeResponse exeCodeNative(@RequestBody  ExecuteCodeRequest executeCodeRequest) {
        return javaNativeCodeSandBox.executeCode(executeCodeRequest);
    }

    @PostMapping("/exeCodeDocker")
    public ExecuteCodeResponse exeCodeDocker(@RequestBody ExecuteCodeRequest executeCodeRequest) {
        return dockerCodeSandbox.executeCode(executeCodeRequest);
    }
}
