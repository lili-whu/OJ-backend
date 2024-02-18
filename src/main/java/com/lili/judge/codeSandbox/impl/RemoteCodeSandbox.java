package com.lili.judge.codeSandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lili.constant.enums.ErrorCode;
import com.lili.exception.BusinessException;
import com.lili.judge.codeSandbox.CodeSandBox;
import com.lili.judge.codeSandbox.model.ExecuteCodeRequest;
import com.lili.judge.codeSandbox.model.ExecuteCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RemoteCodeSandbox implements CodeSandBox{


    // 定义鉴权请求头 + 密钥
    public static final String AUTH_REQUEST_HEADER = "auth";

    public static final String AUTH_REQUEST_SECRET = "secret";

    // Value注解不能是static final,并且只能通过spring bean管理
    @Value("${codeSandbox.url}")
    private String url;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if(StringUtils.isBlank(responseStr)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "代码沙箱API调用错误");
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
