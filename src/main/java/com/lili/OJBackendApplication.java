package com.lili;

import com.lili.rabbitmq.InitMQ;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lili.mapper")
public class OJBackendApplication{

    public static void main(String[] args){
        InitMQ.doInitMQ();
        SpringApplication.run(OJBackendApplication.class, args);
    }

}
