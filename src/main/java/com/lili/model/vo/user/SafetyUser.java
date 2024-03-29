package com.lili.model.vo.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SafetyUser implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type= IdType.AUTO)
    private Long id;

    private String username;

    private String userAccount;

    private String avatar;

    private Integer userRole;

    private Integer gender;

    private String phone;

    private String email;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
