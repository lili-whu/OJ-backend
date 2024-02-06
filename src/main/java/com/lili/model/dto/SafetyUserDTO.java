package com.lili.model.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SafetyUserDTO{

    private Long id;

    private String username;

    private String userAccount;

    private String avatar;

    private Integer userRole;

    private Integer gender;

    private String phone;

    private String email;

    private Integer status;
}
