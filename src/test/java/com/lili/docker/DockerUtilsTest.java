package com.lili.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.WaitResponse;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class DockerUtilsTest{

    @Test
    public void dockerTest() throws InterruptedException{
        DockerUtils dockerUtils = new DockerUtils();

        DockerClient dockerClient = dockerUtils.connectDocker("tcp://121.199.10.73:2375");

        // 拉取镜像
//        dockerClient.pullImageCmd("hello-world");
        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd("hello-world");
        CreateContainerResponse createContainerResponse = containerCmd.exec();
        System.out.println("createContainerResponse = " + createContainerResponse);

        // 查看容器状态
//        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
//        List<Container> containerList = listContainersCmd.withShowAll(true).exec();
//        containerList.forEach(System.out::println);

        //得到容器
        String containerId = createContainerResponse.getId();

        //启动容器
        dockerClient.startContainerCmd(containerId).exec();
        // 创建log回调函数
        LogContainerResultCallback resultCallback = new LogContainerResultCallback(){
            @Override
            public void onNext(Frame item){
                System.out.print("item.getPayload() = " + new String(item.getPayload()));
//                System.out.println("item.getStreamType() = " + item.getStreamType());
                super.onNext(item);
            }
        };
        dockerClient.logContainerCmd(containerId)
                .withStdErr(true).withStdOut(true)
                .exec(resultCallback).awaitCompletion();

        dockerClient.removeContainerCmd(containerId).exec();
    }
}