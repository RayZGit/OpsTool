package com.rayzhou.usercenter.service;

import com.rayzhou.usercenter.model.User;
import jakarta.annotation.Resource;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUserAccount("1234");
        user.setUserName("rayz_new");
        user.setAvatarUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/e/e8/Tesla_logo.png/600px-Tesla_logo.png");
        user.setGender(1);
        user.setUserPassword(DigestUtils.md5DigestAsHex(("ray" + "123456").getBytes()));
        user.setPhone("1231234");
        user.setEmail("xxx@gmail.com");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);

    }

    @Test
    public void testRegisterUser() {
        String userAccount = "rayzsss";
        String password = "12345678";
        String checkPassword = "12345678";
        long result = userService.registerUser(userAccount, password, checkPassword);
        System.out.println(result);



    }

}