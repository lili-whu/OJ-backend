package com.lili.constant.enums;

public enum JudgeInfoMessage{

    ACCEPTED("成功", "ACCEPTED"),
    WRONG_ANSWER("答案错误", "WRONG_ANSWER"),
    MEMORY_LIMIT_EXCEEDED("内存溢出", "Memory_LIMIT_EXCEEDED"),
    COMPILE_ERROR("编译错误", "COMPILE_ERROR"),
    TIME_LIMIT_EXCEEDED("时间溢出", "TIME_LIMIT_EXCEEDED"),
    OUTPUT_LIMIT_EXCEEDED("输出溢出", "OUTPUT_LIMIT_EXCEEDED"),
    RUNTIME_ERROR("运行错误", "RUNTIME_ERROR"),
    SYSTEM_ERROR("系统错误", "SYSTEM_ERROR"),
    DANGEROUS_ERROR("危险操作", "DANGEROUS_ERROR");

    public String getValue(){
        return value;
    }

    public String getText(){
        return text;
    }

    private final String text;
    private final String value;


    JudgeInfoMessage(String text, String value){
        this.text = text;
        this.value = value;
    }

    JudgeInfoMessage getEnumByValue(String value){
        for(JudgeInfoMessage e: JudgeInfoMessage.values()){
            if(e.getValue().equals(value)) return e;
        }
        return null;
    }
}
