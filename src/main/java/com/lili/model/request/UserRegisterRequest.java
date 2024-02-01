package com.lili.model.request;


import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest{
    private String userAccount;

    private String password;
}
