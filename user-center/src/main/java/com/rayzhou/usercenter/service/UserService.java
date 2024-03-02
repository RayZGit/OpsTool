package com.rayzhou.usercenter.service;

import com.rayzhou.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author Ray
* @description 针对表【user】的数据库操作Service
* @createDate 2024-02-12 02:14:10
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param password 用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long registerUser(String userAccount, String password, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     *  根据用户名查找用户
     * @param userName
     * @return 用户数组
     */
    List<User> searchUsers(String userName);

    /**
     * 脱敏用户信息
     * @param user 用户
     * @return 用户
     */
    User getSafetyUser(User user);

    /**
     *  用户注销
     * @param request
     */
    void userLogout(HttpServletRequest request);
}
