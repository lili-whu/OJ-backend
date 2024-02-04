package com.lili.service;

import com.lili.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * 用户服务测试
 * @Author lili
 */
@SpringBootTest
@Slf4j
class UserServiceTest{

    @Autowired
    private UserService userService;
    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("lili");
        user.setUserAccount("1048221");
        user.setAvatar("https://i2.hdslb.com/bfs/face/9c7b650c13cc0febf314dc26062a3d9c670c79c1.jpg@92w_92h.avif");
        user.setGender(1);
        user.setPassword("2132112");
        user.setPhone("19832787427");
        user.setEmail("2131223@qq.com");
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setIsDelete(0);

        boolean save = userService.save(user);
        System.out.println("user.getId() = " + user.getId());
        Assertions.assertTrue(save);

    }

    @Test
    void userRegister(){
        // 校验
        String accountName = "lilia";
        String password = "123456abc";
        String confirmPassword = password;
        Long result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertEquals(-1, result);
        accountName = "liliabc??";
        password = "123456abc";
        confirmPassword = password;
        result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertEquals(-1, result);
        accountName = "liliabc";
        password = "1234a";
        confirmPassword = password;
        result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertEquals(-1, result);
        accountName = "liliabc";
        password = "123433423";
        confirmPassword = password;
        result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertEquals(-1, result);
        accountName = "liliabc";
        password = "abcdefg";
        confirmPassword = password;
        result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertEquals(-1, result);
        accountName = "liliabc";
        password = "1234abc";
        confirmPassword = password;
        result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertTrue(result > 0);
        // 账户名不能重复
        accountName = "liliabc";
        password = "1234abc";
        confirmPassword = password;
        result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertEquals(-1, result);

    }
    @Test
    public void testAccountUnique(){
        String accountName = "liliabc123";
        String password = "lili1234abc";
        String confirmPassword = password;
        Long result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertTrue(result > 0);
        // 账户名不能重复
        accountName = "liliabc123";
        password = "lili1234abc";
        confirmPassword = password;
        result = userService.userRegister(accountName, password, confirmPassword);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void testUserRegister(){
    }

    @Test
    void userLogin(){

    }
}