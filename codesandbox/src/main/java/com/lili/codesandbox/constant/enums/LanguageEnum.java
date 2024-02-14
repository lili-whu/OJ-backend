package com.lili.codesandbox.constant.enums;


/**
 * 编程语言
 */
public enum LanguageEnum{
    JAVA(1, "Java"),
    CPLUSPLUS(2, "C++"),
    GOLANG(3, "Golang");

    private final Integer code;

    private final String lang;


    LanguageEnum(int code, String lang){
        this.code = code;
        this.lang = lang;
    }

    public String getLang(){
        return lang;
    }

    public Integer getCode(){
        return code;
    }
}
