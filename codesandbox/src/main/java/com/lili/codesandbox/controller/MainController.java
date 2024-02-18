package com.lili.codesandbox.controller;


import com.lili.codesandbox.codeSandbox.DockerCodeSandboxImpl;
import com.lili.codesandbox.codeSandbox.JavaNativeCodeSandBoxImpl;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeRequest;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MainController{

    // 定义鉴权请求头 + 密钥
    public static final String AUTH_REQUEST_HEADER = "auth";

    public static final String AUTH_REQUEST_SECRET = "secret";

    @Resource
    DockerCodeSandboxImpl dockerCodeSandbox = new DockerCodeSandboxImpl();

    @Resource
    JavaNativeCodeSandBoxImpl javaNativeCodeSandBox = new JavaNativeCodeSandBoxImpl();


    @PostMapping("/exeCodeNative")
    public ExecuteCodeResponse exeCodeNative(@RequestBody  ExecuteCodeRequest executeCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if(!AUTH_REQUEST_SECRET.equals(authHeader)){
            response.setStatus(403);
            return null;
        };
        return javaNativeCodeSandBox.executeCode(executeCodeRequest);
    }

    @PostMapping("/exeCodeDocker")
    public ExecuteCodeResponse exeCodeDocker(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if(!AUTH_REQUEST_SECRET.equals(authHeader)){
            response.setStatus(403);
            return null;
        };
        return dockerCodeSandbox.executeCode(executeCodeRequest);
    }
}
