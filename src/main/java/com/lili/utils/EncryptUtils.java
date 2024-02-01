package com.lili.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Base64;

public class EncryptUtils{

    private static final String salt = "this_is_a_password_salt";
    public static String passwordEncrypt(String password){
        return Base64.getEncoder().encodeToString(DigestUtils.sha256((salt + password).getBytes()));
    }
}
