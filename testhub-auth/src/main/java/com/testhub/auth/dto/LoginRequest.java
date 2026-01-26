package com.testhub.auth.dto;

import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author TestHub Team
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
