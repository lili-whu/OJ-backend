package com.lili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lili.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{

}