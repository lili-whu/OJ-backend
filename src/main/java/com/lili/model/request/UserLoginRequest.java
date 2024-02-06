package com.lili.model.request;


import lombok.Data;

@Data
public class UserLoginRequest{
    private String userAccount;

    private String password;
}
