package com.lili.codesandbox.codeSandbox;

import cn.hutool.json.JSONUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Version;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;

@Slf4j
public class DockerUtils{


    @Value("${docker}")
    private static final String dockerPort = "tcp://121.199.10.73:2375";

    /**
     * 连接Docker服务器
     * @return 连接服务
     */
    public DockerClient connectDocker(){


        DefaultDockerClientConfig custom = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerPort)
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(URI.create(dockerPort))
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(custom).withDockerHttpClient(httpClient).build();
        Version version = dockerClient.versionCmd().exec();
        log.info("版本信息" + JSONUtil.toJsonStr(version));
        return dockerClient;
    }

}