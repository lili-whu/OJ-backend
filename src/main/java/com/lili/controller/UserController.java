package com.lili.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lili.constant.enums.RoleEnum;
import com.lili.model.Result;
import com.lili.model.User;
import com.lili.model.request.UserLoginRequest;
import com.lili.model.request.UserRegisterRequest;
import com.lili.service.UserService;
import com.lili.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户Controller接口
 * @Author lili
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController{

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * 注册接口
     * @param request 注册参数
     * @return 用户id
     */
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest request){
        log.info("user register:{}", request);
        Long id = userService.userRegister(request.getUserAccount(), request.getPassword());
        return Result.success(id);
    }

    /**
     * 登录接口
     * @param request 登录参数
     * @param httpServletRequest http请求头
     * @return 安全的User返回
     */
    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserLoginRequest request, HttpServletRequest httpServletRequest){
        log.info("user login:{}", request);
        User user = userService.userLogin(request.getUserAccount(), request.getPassword(), httpServletRequest);
        return Result.success(user);
    }

    /**
     * 根据用户名查询用户(模糊搜索)
     * 管理员方法
     * @param username 用户名
     * @return 用户集合
     */
    @GetMapping("/search")
    // todo  需要对管理员方法鉴权(session, interceptor)
    public Result<List<User>> searchUsers(String username){
        log.info("search users by username:{}", username);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username);
        // 对数据进行脱敏
        List<User> safeUserList = userService.list(queryWrapper)
                .stream().map(userService::getSafeUser).toList();
        return Result.success(safeUserList);
    }

    /**
     * 删除用户
     * @param id 删除用户id
     * @return 是否删除成功
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id){
        log.info("delete user by id:{}", id);
        if(id <= 0) return Result.error("invalid id");
        // 逻辑删除
        return Result.success(userService.removeById(id));
    }
}
