package com.rayzhou.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rayzhou.usercenter.common.BaseResponse;
import com.rayzhou.usercenter.common.ErrorCode;
import com.rayzhou.usercenter.exception.BusinessException;
import com.rayzhou.usercenter.model.User;
import com.rayzhou.usercenter.model.request.UserLoginRequest;
import com.rayzhou.usercenter.model.request.UserRegisterRequest;
import com.rayzhou.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.rayzhou.usercenter.common.ResultUtils.createSuccessResponse;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        String userAccount = request.getUserAccount();
        String password = request.getPassword();
        String checkPassword = request.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        long result = userService.registerUser(userAccount, password, checkPassword);
        return createSuccessResponse(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }

        User user = userService.userLogin(userAccount, password, request);

        return createSuccessResponse(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        userService.userLogout(request);
        return createSuccessResponse(1);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUse(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) obj;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        User currentUser = userService.getById(userId);
        // TODO: User authorization
        if (currentUser != null && currentUser.getIsDelete() == 1) {
            log.info("User: {} is deleted", user);
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        return createSuccessResponse(userService.getSafetyUser(user));
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> users = userService.list(queryWrapper);
        return createSuccessResponse(users.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList()));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(id <= 0) {
            throw new BusinessException(ErrorCode.INPUT_ERROR);
        }
        return createSuccessResponse(userService.removeById(id));
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
