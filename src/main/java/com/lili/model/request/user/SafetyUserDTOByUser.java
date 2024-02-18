package com.lili.model.request.user;

import lombok.Data;

@Data
public class SafetyUserDTOByUser{
    private String username;

    private String userAccount;

    private String avatar;

    private Integer gender;

    private String phone;

    private String email;
}
