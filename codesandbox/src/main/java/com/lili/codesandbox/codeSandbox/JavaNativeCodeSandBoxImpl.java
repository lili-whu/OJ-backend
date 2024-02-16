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
import com.lili.codesandbox.utils.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JavaNativeCodeSandBoxImpl implements CodeSandBox{
    // todo 这里是OJ的子模块, 路径有一定问题
    public static final String GLOBAL_CODE_DIR = "codesandbox/testcode";

    public static final String GLOBAL_FILE_NAME = "Main.java";

    public static final List<String> blackList = Arrays.asList("File", "exec", "Paths");

    public static final WordTree wordTree;


    // 静态方法初始化
    static {
        // 初始化字典树
        wordTree = new WordTree();
        wordTree.addWords(blackList);

    }


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();

        // 检查代码非法操作
        FoundWord foundWord = wordTree.matchWord(code);
        if(foundWord != null){
            System.out.println("存在非法字符: " + foundWord.getFoundWord());
            return getErrorResponse("非法操作");
        }

        //1. 保存用户代码文件
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR;
        // 判断代码目录是否存在
        if(!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }
        // 用户代码隔离
        String userCodeDir = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeDir + File.separator + GLOBAL_FILE_NAME;
        File file = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        // 2. 编译用户代码, 生成class文件
        String compileCmd = String.format("javac -encoding utf-8 %s", file.getAbsolutePath());
        ExecuteMessage executeMessage;
        try {
            executeMessage = ProcessUtils.runProcess(compileCmd);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return getErrorResponse("系统执行错误");
        }
        if(executeMessage.getErrorMessage() != null){
            System.out.println(executeMessage.getErrorMessage());
            return getErrorResponse("编译错误");
        }
        System.out.println("compileExecuteMessage = " + executeMessage);

        // 3. 执行代码, 获取输出结果
        List<ExecuteMessage> executeMessages = new ArrayList<>();

        for(String inputArgs: inputList){
            String runCmd = String.format("java -Xmx256m -cp %s Main %s", userCodeDir, inputArgs);
            try {
                executeMessage = ProcessUtils.runProcess(runCmd);
            } catch (Exception e) {
                return getErrorResponse("系统执行错误");
            }
            System.out.println("runExecuteMessage = " + executeMessage);
            executeMessages.add(executeMessage);
        }

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();


        // 4. 整理输出结果
        long maxTime = 0; // 执行测试用例所需的最长时间, 用于判断是否超时
        List<String> outputList = new ArrayList<>();
        for(ExecuteMessage message: executeMessages){
            if(StringUtils.isNotBlank(message.getErrorMessage())){
                executeCodeResponse.setMessage(message.getErrorMessage());
                // 代码执行报错, status为3
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            maxTime = Math.max(maxTime, executeMessage.getTime());
        }
        if(outputList.size() == executeMessages.size()){
            // 正常执行返回
            executeCodeResponse.setStatus(1);
        }

        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
//        judgeInfo.setMessage();
//        judgeInfo.setMemoryConsume();
        judgeInfo.setTimeConsume(maxTime);

        executeCodeResponse.setJudgeInfo(judgeInfo);

        //5. 文件清理
        boolean del = FileUtil.del(userCodeDir);
        System.out.println("文件清理" + (del?"成功":"失败"));

        return executeCodeResponse;
    }

    private ExecuteCodeResponse getErrorResponse(String message){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setMessage(message);
        executeCodeResponse.setStatus(2);
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        return executeCodeResponse;
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
