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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandBox{
    public static final String GLOBAL_CODE_DIR = "codesandbox/testcode";

    public static final String GLOBAL_FILE_NAME = "Main.java";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        ExecuteCodeResponse outputResponse;
        try{
            //0. 初始化操作
            zeroOperation(executeCodeRequest);

            //1. 保存用户代码文件
            File file = saveCodeToFile(code);

            // 2. 编译用户代码, 生成class文件
            compileFile(file);

            // 3. 执行代码, 获取输出结果
            List<ExecuteMessage> executeMessages = runJavaCode(file, inputList);

            // 4. 整理输出结果
            outputResponse = getOutputResponse(executeMessages);

            //5. 文件清理
            deleteFile(file);
        }catch(CodeSandboxException e){
            return getErrorResponse(e);
        }catch (Exception e){
            e.printStackTrace();
            CodeSandboxException codeSandboxException = new CodeSandboxException("其他错误", CodeSandboxStatusEnum.SYSTEM_ERROR.getCode());
            return getErrorResponse(codeSandboxException);
        }
        return outputResponse;
    }

    /**
     * 可能需要的初始化操作
     * @param executeCodeRequest 用户请求
     */
    protected void zeroOperation(ExecuteCodeRequest executeCodeRequest){
    }

    /**
     * 用户代码保存为文件
     * @param code 用户代码
     * @return 保存后的代码文件
     */
    protected File saveCodeToFile(String code){
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR;
        // 用户代码隔离
        String userCodeDir = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeDir + File.separator + GLOBAL_FILE_NAME;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    /**
     * javac编译代码文件
     *
     * @param userCodeFile 用户代码文件
     */
    protected void compileFile(File userCodeFile){
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        ExecuteMessage executeMessage;
        try {
            executeMessage = ProcessUtils.runProcess(compileCmd);
        } catch (InterruptedException | IOException e) {
            throw new CodeSandboxException("系统错误", CodeSandboxStatusEnum.SYSTEM_ERROR.getCode());
        }
        if(executeMessage.getErrorMessage() != null){
            System.out.println(executeMessage.getErrorMessage());
            throw new CodeSandboxException(executeMessage.getErrorMessage(), CodeSandboxStatusEnum.COMPILE_ERROR.getCode());
        }
    }

    /**
     * 创建进程, 运行Java程序
     * @param compiledFile 编译后文件
     * @param inputList 测试用例
     * @return 结果集合
     */
    protected List<ExecuteMessage> runJavaCode(File compiledFile, List<String> inputList){
        List<ExecuteMessage> executeMessages = new ArrayList<>();
        for(String inputArgs: inputList){
            String runCmd = String.format("java -Xmx256m -cp %s Main %s", compiledFile.getParentFile().getAbsolutePath(), inputArgs);
            ExecuteMessage executeMessage;
            try {
                executeMessage = ProcessUtils.runProcess(runCmd);
            } catch (Exception e) {
                throw new CodeSandboxException("系统执行异常", CodeSandboxStatusEnum.SYSTEM_ERROR.getCode());
            }
            executeMessage.setMemory(0L);
            executeMessages.add(executeMessage);
        }
        return executeMessages;

    }


    /**
     * 4. 根据代码执行, 整理输出结果
     * @param executeMessages 代码执行结果
     * @return 整理后的返回
     */
    protected ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessages){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        // 整理输出结果
        long maxTime = 0; // 执行测试用例所需的最长时间, 用于判断是否超时
        long maxMemory = 0; // 消耗内存
        List<String> outputList = new ArrayList<>();
        for(ExecuteMessage message: executeMessages){
            if(StringUtils.isNotBlank(message.getErrorMessage())){
                // 代码执行报错
                throw new CodeSandboxException(message.getErrorMessage(), CodeSandboxStatusEnum.RUNTIME_ERROR.getCode());
            }
            outputList.add(message.getMessage());
            maxTime = Math.max(maxTime, message.getTime());
            maxMemory = Math.max(maxMemory, message.getMemory());
        }
        executeCodeResponse.setStatus(CodeSandboxStatusEnum.CORRECT.getCode());
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemoryConsume(maxMemory);
        judgeInfo.setTimeConsume(maxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    /**
     * 执行完成后清理编译文件
     * @param userCodeFile 用户代码文件
     */
    protected void deleteFile(File userCodeFile){
        boolean del = FileUtil.del(userCodeFile.getParentFile().getAbsolutePath());
//        log.info("文件清理" + (del?"成功":"失败"));
    }

    /**
     * 返回错误结果
     * @param e 代码沙箱执行异常
     * @return 沙箱执行返回
     */
    protected ExecuteCodeResponse getErrorResponse(CodeSandboxException e){
            ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
            executeCodeResponse.setMessage(e.getMessage());
            executeCodeResponse.setStatus(e.getStatus());
            return executeCodeResponse;
        }

    /**
     * 测试抽象代码沙箱执行
     * @param args 测试输入
     */
    public static void main(String[] args){
            // 加入{} , 表示抽象类可以作为匿名内部类中实例化(抽象类中所有方法都有实现)
            JavaCodeSandboxTemplate codeSandBox = new JavaCodeSandboxTemplate(){
            };
            ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
            executeCodeRequest.setInputList(Arrays.asList("1 2", "3 4"));
            String code = ResourceUtil.readStr("testcode/simpleCompute/Main.java", StandardCharsets.UTF_8);
            executeCodeRequest.setLanguage(1);
            executeCodeRequest.setCode(code);
            ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
            System.out.println("executeCodeResponse = " + executeCodeResponse);

        }

}
