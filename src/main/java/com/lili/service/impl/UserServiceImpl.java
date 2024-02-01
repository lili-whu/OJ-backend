package com.lili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lili.constant.StringConstant;
import com.lili.model.User;
import com.lili.service.UserService;
import com.lili.mapper.UserMapper;
import com.lili.utils.EncryptUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
* @author lili
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-01-31 14:49:02
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper){
        this.userMapper = userMapper;
    }

    /**
     * 用户注册
     * @param userAccount 注册账户名
     * @param password 密码
     * @return 生成的用户id, 返回为Long类型, 如果数据库保存失败, getId返回null不会报错
     */
    @Override
    public Long userRegister(String userAccount, String password){
        // 1. 校验
        if(!verifyAccountAndPassword(userAccount, password)){
            return -1L;
        }
        // 查询账户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);

        long countAccount = userMapper.selectCount(queryWrapper);
        if(countAccount != 0){
            return -1L;
        }
        // 密码加密
        String encryptPassword = EncryptUtils.passwordEncrypt(password);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(encryptPassword);
        // 加入创建时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        // 调用mp中已经定义的service中的save方法保存用户
        this.save(user);
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest httpServletRequest){
        // 1. 校验, 不通过则不需要到数据库中查询
        if(!verifyAccountAndPassword(userAccount, password)){
            // TODO 加入自定义异常
            return null;
        }
        String encryptPassword = EncryptUtils.passwordEncrypt(password);
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user not found");
            return null;
        }
        // 密码对比
        if(!encryptPassword.equals(user.getPassword())){
            log.info("password is incorrect");
            return null;
        }
        // 返回数据脱敏(去除密码等)
        User safetyUser = getSafeUser(user);
        // 记录用户登录态
        httpServletRequest.getSession().setAttribute(StringConstant.USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 对返回用户数据进行脱敏
     * @param originUser 原始数据库中用户
     * @return 脱敏用户
     * // todo 后续新的vo对象
     */
    @Override
    public User getSafeUser(User originUser){
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatar(originUser.getAvatar());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setStatus(originUser.getStatus());
        return safetyUser;
    }

    private boolean verifyAccountAndPassword(String userAccount, String password){
        if(userAccount == null && password == null){
            return false;
        }
        if(!Pattern.matches("[a-zA-Z1-9_]{6,100}", userAccount)){
            return false;
        }
        // 密码至少包含字母和数字, 6位以上
        if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,100}$", password)){
            return false;
        }
        return true;
    }
}




