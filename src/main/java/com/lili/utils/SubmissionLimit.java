package com.lili.utils;

import com.lili.constant.StringConstant;
import com.lili.model.vo.user.SafetyUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SubmissionLimit{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean trySubmit(HttpServletRequest httpServletRequest) {
        SafetyUser safetyUser = (SafetyUser) httpServletRequest.getSession().getAttribute(StringConstant.USER_LOGIN_STATE);
        Long userId = safetyUser.getId();

        String key = "submitLimit:" + userId;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        // 使用 SETNX 来检查键是否存在，同时设置过期时间为 10 秒
        Boolean success = ops.setIfAbsent(key, "1", 10, TimeUnit.SECONDS);

        // 如果 SETNX 返回 true，说明之前没有提交过，允许本次提交
        // 如果返回 false，说明已经提交过，本次提交不允许
        return Boolean.TRUE.equals(success);
    }
}
