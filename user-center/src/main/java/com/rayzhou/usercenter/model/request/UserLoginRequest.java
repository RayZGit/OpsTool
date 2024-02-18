package com.rayzhou.usercenter.model.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8364164262284365132L;

    private String userAccount;
    private String password;

}
