package com.rayzhou.usercenter.model.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class UserRegisterRequest implements Serializable {


    @Serial
    private static final long serialVersionUID = 5264478612980303171L;

    private String userAccount;
    private String password;
    private String checkPassword;
}
