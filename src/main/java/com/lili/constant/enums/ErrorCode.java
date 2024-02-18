package com.lili.constant.enums;

import lombok.Getter;

/**
 * 通用错误码, 没有定义错误码的含义
 */
@Getter
public enum ErrorCode{
    SUCCESS(20000), // 正常响应
    SYSTEM_ERROR(50000), // 系统内部异常
    UN_AUTH(40100), // 无权限
    NOT_ADMIN(40102), // 非管理员
    UN_LOGIN(40101), // 未登录
    PARAMS_ERROR(40001), // 参数错误
    NOT_FOUND(40400), //未发现
    API_REQUEST_ERROR(50010), // API请求错误
    SUBMIT_TOO_MUCH(40000), // 提交请求过多
    NULL_PARAM(40002); // 参数为空


    private final int code;

    //给枚举值定义的构造方法
    ErrorCode(int code){
        this.code = code;
    }

}
