package com.lili.model.request.user;


import lombok.Data;

@Data
public class UserLoginRequest{
    private String userAccount;

    private String password;
}
