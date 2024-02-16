package com.lili.constant.enums;

import org.springframework.data.relational.core.sql.In;

public enum JudgeInfoMessage{

    ACCEPTED("成功", "ACCEPTED", 1),
    WRONG_ANSWER("答案错误", "WRONG_ANSWER", 2),
    MEMORY_LIMIT_EXCEEDED("内存溢出", "Memory_LIMIT_EXCEEDED", 3),
    COMPILE_ERROR("编译错误", "COMPILE_ERROR", 4),
    TIME_LIMIT_EXCEEDED("时间溢出", "TIME_LIMIT_EXCEEDED", 5),
    OUTPUT_LIMIT_EXCEEDED("输出溢出", "OUTPUT_LIMIT_EXCEEDED", 6),
    RUNTIME_ERROR("运行错误", "RUNTIME_ERROR", 7),
    SYSTEM_ERROR("系统错误", "SYSTEM_ERROR", 8),
    DANGEROUS_ERROR("危险操作", "DANGEROUS_ERROR", 9);

    public String getValue(){
        return value;
    }

    public String getText(){
        return text;
    }

    private final String text;
    private final String value;


    private final Integer code;
    public Integer getCode(){
        return code;
    }




    JudgeInfoMessage(String text, String value, Integer code){
        this.text = text;
        this.value = value;
        this.code = code;
    }

    public static JudgeInfoMessage getEnumByValue(String value){
        for(JudgeInfoMessage e: JudgeInfoMessage.values()){
            if(e.getValue().equals(value)) return e;
        }
        return null;
    }
}
