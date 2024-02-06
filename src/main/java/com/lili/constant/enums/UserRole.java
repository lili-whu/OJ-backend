package com.lili.constant.enums;

/**
 * 用户权限
 */
public enum UserRole{
    /**
     * 普通用户权限
     */
    DEFAULT_ROLE(0),
    /**
     * 管理员用户权限
     */
    ADMIN_ROLE(1);

    public final int role;

    public int getRole(){
        return role;
    }

    UserRole(int role){
        this.role = role;
    }
}
