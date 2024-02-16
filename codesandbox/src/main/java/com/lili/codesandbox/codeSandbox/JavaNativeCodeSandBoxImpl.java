package com.lili.codesandbox.codeSandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeRequest;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeResponse;
import com.lili.codesandbox.codeSandbox.model.ExecuteMessage;
import com.lili.codesandbox.codeSandbox.model.JudgeInfo;
import com.lili.codesandbox.enums.CodeSandboxStatusEnum;
import com.lili.codesandbox.exception.CodeSandboxException;
import com.lili.codesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class JavaNativeCodeSandBoxImpl extends JavaCodeSandboxTemplate{

    public static final List<String> blackList = Arrays.asList("File", "exec", "Paths");

    public static final WordTree wordTree;


    // 静态方法初始化
    static {
        // 初始化字典树
        wordTree = new WordTree();
        wordTree.addWords(blackList);

    }

    @Override
    protected void zeroOperation(ExecuteCodeRequest executeCodeRequest){
        String code = executeCodeRequest.getCode();
        // 检查代码非法操作
        FoundWord foundWord = wordTree.matchWord(code);
        if (foundWord != null) {
            log.info("存在非法字符: " + foundWord.getFoundWord());
            throw new CodeSandboxException("非法操作", CodeSandboxStatusEnum.COMPILE_ERROR.getCode());
        }
    }

    public static void main(String[] args){
        JavaNativeCodeSandBoxImpl codeSandBox = new JavaNativeCodeSandBoxImpl();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "3 4"));
        String code = ResourceUtil.readStr("testcode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setLanguage(1);
        executeCodeRequest.setCode(code);
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        System.out.println("executeCodeResponse = " + executeCodeResponse);

    }
}
