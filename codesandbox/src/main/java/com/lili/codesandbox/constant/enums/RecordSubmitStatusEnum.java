package com.lili.codesandbox.constant.enums;

public enum RecordSubmitStatusEnum{

    WAITING("等待中", 0),

    RUNNING("判题中", 1),

    SUCCESS("判题完成", 2),

    FAILED("判题失败", 3);
    private final String info;

    private final int status;

    public String getInfo(){
        return info;
    }

    public int getStatus(){
        return status;
    }

    RecordSubmitStatusEnum(String info, int status){
        this.info = info;
        this.status = status;
    }


}
