package com.rayzhou.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rayzhou.usercenter.model.User;
import com.rayzhou.usercenter.model.request.UserLoginRequest;
import com.rayzhou.usercenter.model.request.UserRegisterRequest;
import com.rayzhou.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rayzhou.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest request) {
        if (request == null) {
            return null;
        }
        String userAccount = request.getUserAccount();
        String password = request.getPassword();
        String checkPassword = request.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {
            return null;
        }

        return userService.registerUser(userAccount, password, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return new User();
        }
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(userAccount, password)) {
            return new User();
        }

        return userService.userLogin(userAccount, password, request);
    }

    @GetMapping("/current")
    public User getCurrentUse(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) obj;
        if (user == null) {
            return null;
        }
        Long userId = user.getId();
        User currentUser = userService.getById(userId);
        // TODO: User authorization
        if (currentUser != null && currentUser.getIsDelete() == 1) {
            log.info("User: {} is deleted", user);
            return null;
        }
        return userService.getSafetyUser(user);
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> users = userService.list(queryWrapper);
        return users.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Boolean.FALSE;
        }
        if(id <= 0) {
            return Boolean.FALSE;
        }
        return userService.removeById(id);
    }

    private boolean isAdmin(HttpServletRequest request) {
        // Authorization
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getUserRole() != 1) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
