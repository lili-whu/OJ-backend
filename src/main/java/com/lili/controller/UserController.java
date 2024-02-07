package com.lili.controller;


import com.lili.annotation.UserRoleAnnotation;
import com.lili.constant.StringConstant;
import com.lili.constant.enums.ErrorCode;
import com.lili.constant.enums.UserRole;
import com.lili.exception.BusinessException;
import com.lili.model.Result;
import com.lili.model.User;
import com.lili.model.dto.SafetyUserDTO;
import com.lili.model.request.UserLoginRequest;
import com.lili.model.request.UserRegisterRequest;
import com.lili.model.vo.PageSafetyUserVO;
import com.lili.model.vo.SafetyUser;
import com.lili.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller接口
 * @Author lili
 */
@RestController
@RequestMapping("api/user")
@Slf4j
@CrossOrigin
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
        Long id = userService.userRegister(request.getUserAccount(), request.getPassword(), request.getConfirmPassword());
        return Result.success(id);
    }


    @GetMapping("/currentUser")
    public Result<SafetyUser> getCurrentUser(HttpServletRequest httpServletRequest){
        log.info("getCurrentUser");
        SafetyUser safetyUser = (SafetyUser) httpServletRequest.getSession().getAttribute(StringConstant.USER_LOGIN_STATE);
        return Result.success(safetyUser);
    }

    /**
     * 登录接口
     * @param request 登录参数
     * @param httpServletRequest http请求头
     * @return 安全的User返回
     */
    // todo Redis单点登录
    @PostMapping("/login")
    public Result<SafetyUser> userLogin(@RequestBody UserLoginRequest request, HttpServletRequest httpServletRequest){
        log.info("user login:{}", request);
        SafetyUser user = userService.userLogin(request.getUserAccount(), request.getPassword(), httpServletRequest);
        return Result.success(user);
    }

    /**
     * 退出登录
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public Result<Integer> userLogout(HttpServletRequest httpServletRequest){
        log.info("用户注销");
        userService.userLogout(httpServletRequest);
        return Result.success();
    }

    /**
     * 根据用户名查询用户(模糊搜索)
     * 管理员方法
     *
     * @return 用户集合
     */
    @PostMapping ("/search")
    @UserRoleAnnotation(UserRole.ADMIN_ROLE)
    public Result<PageSafetyUserVO> searchUsers(int pageSize, int current, @RequestBody SafetyUserDTO safetyUserDTO, HttpServletRequest httpServletRequest){
        log.info("search users by admin:{}", safetyUserDTO);
        PageSafetyUserVO puv = userService.searchUsers(pageSize, current, safetyUserDTO);
        return Result.success(puv);
    }

    /**
     * 删除用户
     * @param id 删除用户id
     * @return 是否删除成功
     */
    @DeleteMapping("/delete/{id}")
    @UserRoleAnnotation(UserRole.ADMIN_ROLE)
    public Result<Boolean> deleteUser(@PathVariable Long id, HttpServletRequest httpServletRequest){
        log.info("delete user by id:{}", id);
        if(id <= 0) throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法id");
        // 逻辑删除
        return Result.success(userService.removeById(id));
    }

    /**
     * 管理员修改用户信息
     * @param user
     * @return
     */
    @PutMapping("/revise")
    @UserRoleAnnotation(UserRole.ADMIN_ROLE)
    public Result<Boolean> reviseUser(User user){
        log.info("revise user by admin: {}", user);

        userService.update();
        return Result.success(true);
    }


}
