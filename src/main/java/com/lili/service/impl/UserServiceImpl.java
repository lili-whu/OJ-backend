package com.lili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lili.constant.StringConstant;
import com.lili.constant.enums.ErrorCode;
import com.lili.exception.BusinessException;
import com.lili.model.User;
import com.lili.model.request.user.SafetyUserDTO;
import com.lili.model.request.user.SafetyUserDTOByUser;
import com.lili.model.vo.user.PageSafetyUserVO;
import com.lili.model.vo.user.SafetyUser;
import com.lili.service.UserService;
import com.lili.mapper.UserMapper;
import com.lili.utils.EncryptUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
* @author lili
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-01-31 14:49:02
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper){
        this.userMapper = userMapper;
    }

    /**
     * 用户注册
     *
     * @param userAccount     注册账户名
     * @param password        密码
     * @param confirmPassword 确认密码
     * @return 生成的用户id, 返回为Long类型, 如果数据库保存失败, getId返回null不会报错
     */
    @Override
    public Long userRegister(String userAccount, String password, String confirmPassword){
        // 1. 校验
        verifyAccountAndPassword(userAccount, password);

        if(!password.equals(confirmPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        // 查询账户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);

        long countAccount = userMapper.selectCount(queryWrapper);
        if(countAccount != 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户名已被占用");
        }
        // 密码加密
        String encryptPassword = EncryptUtils.passwordEncrypt(password);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(encryptPassword);
        // 加入创建时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        // 调用mp中已经定义的service中的save方法保存用户
        this.save(user);
        return user.getId();
    }

    @Override
    public SafetyUser userLogin(String userAccount, String password, HttpServletRequest httpServletRequest){
        // 1. 校验, 不通过则不需要到数据库中查询
        verifyAccountAndPassword(userAccount, password);
        String encryptPassword = EncryptUtils.passwordEncrypt(password);
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不存在");
        }
        // 密码对比
        if(!encryptPassword.equals(user.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户密码错误");
        }
        // 返回数据脱敏(去除密码等)
        SafetyUser safetyUser = getSafeUser(user);
        // 记录用户登录态
        httpServletRequest.getSession().setAttribute(StringConstant.USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 对返回用户数据进行脱敏
     * @param originUser 原始数据库中用户
     * @return 脱敏用户
     */
    @Override
    public SafetyUser getSafeUser(User originUser){
        if(originUser == null) return null;
        SafetyUser safetyUser = new SafetyUser();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatar(originUser.getAvatar());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setStatus(originUser.getStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        return safetyUser;
    }

    @Override
    public void userLogout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().removeAttribute(StringConstant.USER_LOGIN_STATE);
    }

    @Override
    @Transactional
    public void reviseUser(SafetyUserDTO safetyUserDTO, HttpServletRequest request){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        User user = new User();

        if(!Pattern.matches("[a-zA-Z1-9_]{6,20}", safetyUserDTO.getUserAccount())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户名称不符合要求");
        }

        // 查询userAccount是否重复
        queryWrapper.eq("user_account", safetyUserDTO.getUserAccount());
        User examineUser = this.baseMapper.selectOne(queryWrapper);
        if(examineUser != null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已被占用");

        // 指定修改用户
        BeanUtils.copyProperties(safetyUserDTO, user);
        userMapper.updateById(user);

        // 得到管理员信息
        SafetyUser admin = (SafetyUser) request.getSession().getAttribute(StringConstant.USER_LOGIN_STATE);
        // 管理元修改自己时, 修改登陆session
        if(user.getId().equals(admin.getId())){
            User updatedUser = userMapper.selectById(admin.getId());
            // 重新设置session
            request.getSession().setAttribute(StringConstant.USER_LOGIN_STATE, this.getSafeUser(updatedUser));
        }
    }

    @Override
    public PageSafetyUserVO searchUsers(int pageSize, int current, SafetyUserDTO safetyUserDTO){
        IPage<User> iPage = new Page<>();
        iPage.setCurrent(current);
        iPage.setSize(pageSize);

        User user = new User();
        BeanUtils.copyProperties(safetyUserDTO, user);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //Wrapper是抽象类, 可以直接调用子类方法
        wrapper.like(user.getUsername() != null, "username", user.getUsername())
                .like(user.getUserAccount() != null, "user_account", user.getUserAccount())
                .like(user.getPhone() != null, "phone", user.getPhone())
                .like(user.getEmail() != null, "email", user.getEmail())
                .like(user.getAvatar() != null, "avatar", user.getAvatar())
                .eq(user.getId() != null, "id", user.getId())
                .eq(user.getGender() != null, "gender", user.getGender())
                .eq(user.getUserRole() != null, "user_role", user.getUserRole())
                .eq(user.getStatus() != null, "status", user.getStatus());

        List<SafetyUser> lu = this.list(iPage, wrapper).stream().map(this::getSafeUser).toList();
        long total = iPage.getTotal();
        return new PageSafetyUserVO(total, lu);
    }

    @Override
    public SafetyUser getLoginUser(HttpServletRequest httpServletRequest){
        return (SafetyUser) httpServletRequest.getSession().getAttribute(StringConstant.USER_LOGIN_STATE);
    }

    @Override
    public void reviseUserByUser(SafetyUserDTOByUser safetyUserDTOByUser, HttpServletRequest request){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        User user = new User();

        if(!Pattern.matches("[a-zA-Z1-9_]{6,20}", safetyUserDTOByUser.getUserAccount())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户名称不符合要求");
        }

        // 得到id
        SafetyUser safetyUser = (SafetyUser) request.getSession().getAttribute(StringConstant.USER_LOGIN_STATE);
        user.setId(safetyUser.getId());

        // 查询username是否重复
        queryWrapper.eq("user_account", safetyUserDTOByUser.getUserAccount());


        User examineUser = this.baseMapper.selectOne(queryWrapper);
        if(examineUser != null && !examineUser.getId().equals(user.getId())) throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已被占用");


        BeanUtils.copyProperties(safetyUserDTOByUser, user);
        userMapper.updateById(user);
        User updatedUser = userMapper.selectById(user.getId());
        // 重新设置session
        request.getSession().setAttribute(StringConstant.USER_LOGIN_STATE, this.getSafeUser(updatedUser));

    }

    private void verifyAccountAndPassword(String userAccount, String password){
        if(userAccount == null && password == null){
            throw new BusinessException(ErrorCode.NULL_PARAM, "账户密码不能为空");
        }
        if(!Pattern.matches("[a-zA-Z1-9_]{6,20}", userAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户名称不符合要求");
        }
        // 密码至少包含字母和数字, 6位以上
        if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,100}$", password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不符合要求");
        }
    }
}




