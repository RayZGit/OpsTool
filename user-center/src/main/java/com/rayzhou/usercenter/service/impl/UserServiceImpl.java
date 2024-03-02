package com.rayzhou.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rayzhou.usercenter.common.ErrorCode;
import com.rayzhou.usercenter.exception.BusinessException;
import com.rayzhou.usercenter.mapper.UserMapper;
import com.rayzhou.usercenter.model.User;
import com.rayzhou.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.rayzhou.usercenter.constant.UserConstant.USER_LOGIN_STATE;


/**
* @author Ray
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-02-12 02:14:09
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    private static final String SALT = "ray";

    @Resource
    private UserMapper userMapper;

    @Override
    public long registerUser(String userAccount, String password, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {
            throw new BusinessException(ErrorCode.INPUT_NULL);
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        String validPattern = "[$&+,:;=?@#|'<>.^*()%!-]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }

        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }

        String hashedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(hashedPassword);
        if (!this.save(user)) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.INPUT_NULL);
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        if (userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        String validPattern = "[$&+,:;=?@#|'<>.^*()%!-]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }

        String hashedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", hashedPassword);

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("User login failed, userAccount or userPassword can't match");
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }

        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());

        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;

    }

    @Override
    public List<User> searchUsers(String userName) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("userName", userName);
        return this.list(queryWrapper);
    }

    public User getSafetyUser(User user) {
        if (user == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(null);
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setUserPassword(null);
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUpdateTime(user.getUpdateTime());
        return safetyUser;
    }

    @Override
    public void userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }
}




