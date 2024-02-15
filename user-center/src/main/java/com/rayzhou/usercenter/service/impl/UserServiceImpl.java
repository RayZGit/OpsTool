package com.rayzhou.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rayzhou.usercenter.service.UserService;
import com.rayzhou.usercenter.model.User;
import com.rayzhou.usercenter.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Ray
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-02-12 02:14:09
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long registerUser(String userAccount, String password, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (password.length() < 8) {
            return -1;
        }
        String validPattern = "[$&+,:;=?@#|'<>.^*()%!-]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }

        if (!password.equals(checkPassword)) {
            return -1;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        if (userMapper.selectCount(queryWrapper) > 0) {
            return -1;
        }

        String hashedPassword = DigestUtils.md5DigestAsHex(("ray" + password).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(hashedPassword);
        if (!this.save(user)) {
            return -1;
        }
        return user.getId();
    }

    @Override
    public User doLogin(User userAccount, String userPassword) {
        
    }
}




