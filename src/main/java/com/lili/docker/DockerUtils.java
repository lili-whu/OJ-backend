package com.lili.docker;

import cn.hutool.json.JSONUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Version;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;

@Service
public class DockerUtils {

    /**
     * 连接Docker服务器
     * @return
     */



    public DockerClient connectDocker(String dockerInstance){


        DefaultDockerClientConfig custom = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://121.199.10.73:2375")
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(URI.create("tcp://121.199.10.73:2375"))
//                .sslConfig(new S)
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        DockerClient dockerClient = DockerClientBuilder.getInstance(custom).withDockerHttpClient(httpClient).build();
        Version version = dockerClient.versionCmd().exec();
        System.out.println("版本信息" + JSONUtil.toJsonStr(version));
        return dockerClient;
    }

}