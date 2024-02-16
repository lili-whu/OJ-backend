package com.lili.codesandbox.codeSandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.StreamType;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeRequest;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeResponse;
import com.lili.codesandbox.codeSandbox.model.ExecuteMessage;
import com.lili.codesandbox.codeSandbox.model.JudgeInfo;
import com.lili.codesandbox.utils.ProcessUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Deprecated
public class DockerCodeSandboxImpl_OLD implements CodeSandBox{
    public static final String GLOBAL_CODE_DIR = "testcode";

    public static final String GLOBAL_FILE_NAME = "Main.java";

    public static final Long TIME_OUT = 5000L;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();

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

        // 3. 创建容器, 把class文件放到容器内
        DockerUtils dockerUtils = new DockerUtils();

        DockerClient dockerClient = dockerUtils.connectDocker();

        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd("openjdk:17.0.2-jdk-oraclelinux7");
        HostConfig hostConfig = new HostConfig();
        hostConfig.withCpuCount(1L); // 限制Cpu核数
        hostConfig.withMemory(100 * 1000 * 1000L); // 限制内存
        hostConfig.withMemorySwap(0L);
        CreateContainerResponse createContainerResponse = containerCmd.withHostConfig(hostConfig)
                .withAttachStdin(true).withAttachStderr(true)
                .withAttachStdout(true).withTty(true)
                .withNetworkDisabled(true) // 限制网络
                .exec();

        //得到并启动容器
        String containerId = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();



        // 得到容器的log信息, 异步方法, 需要await等待执行结束
//        try {
//            dockerClient.logContainerCmd(containerId)
//                    .withStdErr(true).withStdOut(true)
//                    .exec(resultCallback).awaitCompletion();
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
//                .withCmd("mkdir", "-p", "/app")
//                .exec();
//        String execId = execCreateCmdResponse.getId();
//        try {
//            dockerClient.execStartCmd(execId).exec()
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


        // 压缩文件, 复制文件到远程
        CopyArchiveToContainerCmd copyArchiveToContainerCmd = dockerClient.copyArchiveToContainerCmd(containerId);
        try {
            copyArchiveToContainerCmd.withTarInputStream(createTarInputStream(userCodeDir, "./Main.class"))
                    .withRemotePath("/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        copyArchiveToContainerCmd.exec();
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        // 检测内存占用
        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        final long[] maxMemory = {0L};
        ResultCallbackTemplate<ResultCallback<Statistics>, Statistics> statsCmdCallback = new ResultCallbackTemplate<>(){

            @Override
            public void onNext(Statistics statistics){
                // 定时检测内存占用
                System.out.println("内存占用"+ statistics.getMemoryStats().getUsage());
                maxMemory[0] = Math.max(maxMemory[0], statistics.getMemoryStats().getUsage());
            }
        };
        statsCmd.exec(statsCmdCallback);


        List<ExecuteMessage> executeMessages = new ArrayList<>();
        try{
        for(String inputArgs: inputList){
            ExecuteMessage exeMessage = new ExecuteMessage();
            String[] s = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/", "Main"}, s);
            StopWatch stopWatch = new StopWatch();
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true) // 创建标准输入输出错误
                    .exec();

            final boolean[] timeout = {true};
            ResultCallbackTemplate<ResultCallback<Frame>, Frame> resultCallback = new ResultCallbackTemplate<>(){
                @Override
                public void onComplete(){
                    timeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame){
                    if(StreamType.STDERR.equals(frame.getStreamType())){
                        exeMessage.setErrorMessage(new String(frame.getPayload()).strip());
                    }else{
                        exeMessage.setMessage(new String(frame.getPayload()).strip());
                    }
                }
            };


            String execId = execCreateCmdResponse.getId();
            stopWatch.start();
            dockerClient.execStartCmd(execId)
                        .exec(resultCallback).awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
            stopWatch.stop();
            // 获取消耗时间
            exeMessage.setTime(stopWatch.getTotalTimeMillis());
            executeMessages.add(exeMessage);
            if(timeout[0]){
                return getErrorResponse("执行超出允许的最大时间");
            }
        }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 执行结束删除容器
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();

    ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        // 整理输出结果
        long maxTime = 0; // 执行测试用例所需的最长时间, 用于判断是否超时
        List<String> outputList = new ArrayList<>();
        for(ExecuteMessage message: executeMessages){
            if(StringUtils.isNotBlank(message.getErrorMessage())){
                executeCodeResponse.setMessage(message.getErrorMessage());
                // 代码执行报错, status为3
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(message.getMessage());
            maxTime = Math.max(maxTime, message.getTime());
        }
        // 结果数量 == 输入数量 正常执行返回
        if(outputList.size() == executeMessages.size()){
            executeCodeResponse.setStatus(1);
        }
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
//        judgeInfo.setMessage();
        judgeInfo.setMemoryConsume(maxMemory[0]);
        judgeInfo.setTimeConsume(maxTime);

        executeCodeResponse.setJudgeInfo(judgeInfo);


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
        DockerCodeSandboxImpl_OLD codeSandBox = new DockerCodeSandboxImpl_OLD();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("4 4", "3 4", "7 8"));
        String code = ResourceUtil.readStr("testcode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setLanguage(1);
        executeCodeRequest.setCode(code);
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        System.out.println("executeCodeResponse = " + executeCodeResponse);

    }


    // 创建字节输出流，用于写入 TAR 数据
    public InputStream createTarInputStream(String userCodeDir, String fileName) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
    TarArchiveOutputStream tarOut = new TarArchiveOutputStream(out);

    // 创建文件引用，指向要打包的文件
    File fileToTar = new File(userCodeDir, fileName);
    FileInputStream fis = new FileInputStream(fileToTar);

    // 创建 TAR 归档条目，指定文件在 TAR 归档中的名称
    TarArchiveEntry entry = new TarArchiveEntry(fileToTar, fileToTar.getName());
    entry.setSize(fileToTar.length()); // 设置文件大小
    tarOut.putArchiveEntry(entry);

    // 将文件内容写入 TAR 归档
    byte[] buffer = new byte[1024];
    int len;
    while ((len = fis.read(buffer)) > 0) {
        tarOut.write(buffer, 0, len);
    }
    fis.close();

    // 完成条目添加
    tarOut.closeArchiveEntry();

    // 完成归档并关闭输出流
    tarOut.finish();
    tarOut.close();

    // 将字节输出流转换为输入流
    return new ByteArrayInputStream(out.toByteArray());
}
}
