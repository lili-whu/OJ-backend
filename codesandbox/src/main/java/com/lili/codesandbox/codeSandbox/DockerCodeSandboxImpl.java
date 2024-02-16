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
import com.lili.codesandbox.codeSandbox.CodeSandBox;
import com.lili.codesandbox.codeSandbox.DockerUtils;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeRequest;
import com.lili.codesandbox.codeSandbox.model.ExecuteCodeResponse;
import com.lili.codesandbox.codeSandbox.model.ExecuteMessage;
import com.lili.codesandbox.codeSandbox.model.JudgeInfo;
import com.lili.codesandbox.exception.CodeSandboxException;
import com.lili.codesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DockerCodeSandboxImpl extends JavaCodeSandboxTemplate{

    public static final Long TIME_OUT = 5000L;


    /**
     * 创建容器, 把class文件放到容器内, 运行代码, 删除容器
     * @param compiledFile 编译后文件
     * @param inputList 测试用例
     * @return 执行结果列表
     */
    @Override
    protected List<ExecuteMessage> runJavaCode(File compiledFile, List<String> inputList){

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

        // 压缩文件, 复制文件到远程
        CopyArchiveToContainerCmd copyArchiveToContainerCmd = dockerClient.copyArchiveToContainerCmd(containerId);
        try {
            copyArchiveToContainerCmd.withTarInputStream(createTarInputStream(compiledFile.getParentFile().getAbsolutePath(), "./Main.class"))
                    .withRemotePath("/");
        } catch (Exception e) {
            throw new CodeSandboxException("系统错误", 2);
        }
        copyArchiveToContainerCmd.exec();

        // 检测内存占用
        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        final long[] maxMemory = {0L};
        ResultCallbackTemplate<ResultCallback<Statistics>, Statistics> statsCmdCallback = new ResultCallbackTemplate<>(){
            @Override
            public void onNext(Statistics statistics){
                // 定时检测内存占用
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
                            throw new CodeSandboxException(new String(frame.getPayload()).strip(), 3);
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
                exeMessage.setMemory(maxMemory[0]);
                executeMessages.add(exeMessage);
                if(timeout[0]){
                    throw new CodeSandboxException("执行超出允许的最大时间", 3);
                }
            }
        } catch (InterruptedException e) {
            throw new CodeSandboxException("系统错误", 2);
        }
        // 执行结束删除容器
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        return executeMessages;
    }


    public static void main(String[] args){
        DockerCodeSandboxImpl codeSandBox = new DockerCodeSandboxImpl();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("4 4", "3 4", "7 8"));
        String code = ResourceUtil.readStr("testcode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setLanguage(1);
        executeCodeRequest.setCode(code);
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        System.out.println("executeCodeResponse = " + executeCodeResponse);

    }

    /**
     *  压缩tar文件
     * @param userCodeDir 代码目录
     * @param fileName 文件名
     * @return tar打包文件
     * @throws Exception 异常
     */
    private InputStream createTarInputStream(String userCodeDir, String fileName) throws Exception {
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
