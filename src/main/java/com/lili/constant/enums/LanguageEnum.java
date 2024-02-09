package com.lili.constant.enums;


/**
 * 编程语言
 */
public enum LanguageEnum{
    JAVA(0, "Java"),
    CPLUSPLUS(1, "C++"),
    GOLANG(2, "Golang");

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
