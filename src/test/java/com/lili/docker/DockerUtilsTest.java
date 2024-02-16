package com.lili.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class DockerUtilsTest{


    @Value("${docker}")
    private String dockerPort;
    @Test
    public void dockerTest() throws InterruptedException{
        DockerUtils dockerUtils = new DockerUtils();

        DockerClient dockerClient = dockerUtils.connectDocker(dockerPort);

        // 拉取镜像, jdk17, 第一次需要执行
//        dockerClient.pullImageCmd("openjdk:17.0.2-jdk-oraclelinux7");
        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd("openjdk:17.0.2-jdk-oraclelinux7");
        HostConfig hostConfig = new HostConfig();
        hostConfig.withCpuCount(1L); // 限制Cpu核数
        hostConfig.withMemory(100 * 100 * 1000L); // 限制内存
        CreateContainerResponse createContainerResponse = containerCmd
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true) // 创建标准输入输出错误

                .withTty(true) // 创建交互


                .exec();
        System.out.println("createContainerResponse = " + createContainerResponse);

        // 查看容器状态
//        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
//        List<Container> containerList = listContainersCmd.withShowAll(true).exec();
//        containerList.forEach(System.out::println);

        //得到容器
        String containerId = createContainerResponse.getId();

        //启动容器

        dockerClient.startContainerCmd(containerId).exec();
        // 创建ResultCallbackTemplate回调函数
        ResultCallbackTemplate<ResultCallback<Frame>, Frame> resultCallback = new ResultCallbackTemplate<>(){
            @Override
            public void onNext(Frame frame){
                System.out.println("frame.getPayload() = " + new String(frame.getPayload()));
            }
        };
        dockerClient.logContainerCmd(containerId)

                .withStdErr(true).withStdOut(true)
                .exec(resultCallback).awaitCompletion();

        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
    }
}