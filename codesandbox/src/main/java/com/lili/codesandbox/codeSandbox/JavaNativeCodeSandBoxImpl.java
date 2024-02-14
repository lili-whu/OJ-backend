package com.lili.codesandbox.codeSandbox;

import cn.hutool.core.io.FileUtil;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeRequest;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeResponse;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class JavaNativeCodeSandBoxImpl implements CodeSandBox{
    public static final String GLOBAL_CODE_DIR = "tmpcode";
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        Integer language = executeCodeRequest.getLanguage();

        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR;
        // 判断代码目录是否存在
        if(!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }
        // 用户代码隔离
        String userCodeDir = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeDir + File.separator + "Main.java";
        FileUtil.writeString(userCodeDir, userCodePath, StandardCharsets.UTF_8);

        return null;
    }

    public static void main(String[] args){
        JavaNativeCodeSandBoxImpl codeSandBox = new JavaNativeCodeSandBoxImpl();

    }
}
