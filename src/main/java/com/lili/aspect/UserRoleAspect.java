package com.lili.aspect;


import com.lili.annotation.UserRoleAnnotation;
import com.lili.constant.StringConstant;
import com.lili.constant.enums.ErrorCode;
import com.lili.constant.enums.UserRole;
import com.lili.exception.BusinessException;
import com.lili.model.vo.user.SafetyUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * 实现公共字段自动填充逻辑
 */
@Aspect
@Slf4j
@Component
public class UserRoleAspect{
    // 拦截注解的方法
    @Pointcut("execution(* com.lili.controller.*.*(..)) && @annotation(com.lili.annotation.UserRoleAnnotation)")
    public void userRolePointcut(){}

    @Before("userRolePointcut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        log.info("身份验证拦截");

        // 获取数据库操作类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        UserRoleAnnotation userRoleAnnotation = methodSignature.getMethod().getAnnotation(UserRoleAnnotation.class);
        UserRole role = userRoleAnnotation.value();

        // 获取被拦截的数据
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = null;

        for(Object arg: args){
            if(arg instanceof HttpServletRequest){
                request = (HttpServletRequest) arg;
                break;
            }
        }

        // 需要HttpServletRequest获取session验证身份
        if(request == null) throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误");

        // 得到session中存储的User信息
        SafetyUser safetyUser = (SafetyUser) request.getSession().getAttribute(StringConstant.USER_LOGIN_STATE);

        // 没有得到User代表非合法用户
        if(safetyUser == null) throw new BusinessException(ErrorCode.UN_LOGIN, "未登录, 非法用户");
        // 对比是否为管理员身份
        if(role == UserRole.ADMIN_ROLE && safetyUser.getUserRole() != role.getRole()){
            throw new BusinessException(ErrorCode.NOT_ADMIN, "非管理员, 禁止访问");
        }
    }
}
