package com.lili.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    @TableId(value = "id", type= IdType.AUTO)
    private Long id;

    private String username;

    private String userAccount;

    private String avatar;

    private Integer userRole;

    private Integer gender;

    private String password;

    private String phone;

    private String email;

    private Integer status;  // 0 表示正常

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDelete; // 0 表示未删除

    @Serial
    private static final long serialVersionUID = 1L;
}