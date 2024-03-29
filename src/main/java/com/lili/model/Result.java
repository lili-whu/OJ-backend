package com.lili.model;
import com.lili.constant.enums.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;

/**
 * 后端统一返回响应结果
 * @param <T> 响应类型
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String message; //错误信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 20000;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 20000;
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<T>();
        result.message = message;
        result.code = code;
        return result;
    }

}
