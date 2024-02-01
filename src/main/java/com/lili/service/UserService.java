package com.lili.service;

import com.lili.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author lili
* @description 针对表【user】的数据库操作Service
* @createDate 2024-01-31 14:49:02
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount 注册账户名
     * @param password 密码
     * @return 生成的用户id
     */
    Long userRegister(String userAccount, String password);

    User userLogin(String userAccount, String password, HttpServletRequest httpServletRequest);

    User getSafeUser(User originUser);
}
