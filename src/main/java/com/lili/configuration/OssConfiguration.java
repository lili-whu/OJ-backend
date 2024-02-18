package com.lili.configuration;

import com.lili.model.AliOssProperties;
import com.lili.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration{

    /**
     * 创建OSS工具类Bean实例
     */
    @Bean
    public AliOssUtil OSS(AliOssProperties aliOssProperties){
        log.info("初始化OSS工具类对象Bean");
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
