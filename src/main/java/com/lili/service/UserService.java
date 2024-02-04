package com.lili.service;

import com.lili.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lili.model.vo.SafetyUserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author lili
* @description 针对表【user】的数据库操作Service
* @createDate 2024-01-31 14:49:02
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount     注册账户名
     * @param password        密码
     * @param confirmPassword
     * @return 生成的用户id
     */
    Long userRegister(String userAccount, String password, String confirmPassword);

    SafetyUserVO userLogin(String userAccount, String password, HttpServletRequest httpServletRequest);

    SafetyUserVO getSafeUser(User originUser);


    /**
     * 用户注销请求
     * @param httpServletRequest
     * @return
     */
    void userLogout(HttpServletRequest httpServletRequest);
}
