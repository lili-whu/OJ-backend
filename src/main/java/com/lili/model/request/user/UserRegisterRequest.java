package com.lili.model.request.user;


import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest{
    private String userAccount;

    private String password;

    private String confirmPassword;
}
