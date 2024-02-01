package com.lili;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lili.mapper")
public class UserManageBackendApplication{

    public static void main(String[] args){
        SpringApplication.run(UserManageBackendApplication.class, args);
    }

}
