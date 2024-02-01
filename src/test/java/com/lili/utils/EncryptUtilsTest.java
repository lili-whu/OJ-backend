package com.lili.utils;

import org.junit.jupiter.api.Test;


class EncryptUtilsTest{
    @Test
    public void TestPasswordEncrypt(){
        String newPassword = EncryptUtils.passwordEncrypt("123456");
        System.out.println("newPassword = " + newPassword);
    }
}