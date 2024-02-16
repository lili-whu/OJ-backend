package com.lili.constant.enums;

public enum RecordSubmitStatusEnum{

    WAITING("等待中", 1),

    FAILED("判题失败", 2),

    SUCCESS("判题成功", 3);


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
