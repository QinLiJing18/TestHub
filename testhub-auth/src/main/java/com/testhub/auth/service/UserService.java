package com.testhub.auth.service;

import com.testhub.auth.dto.LoginRequest;
import com.testhub.auth.dto.LoginResponse;
import com.testhub.auth.dto.RegisterRequest;
import com.testhub.auth.entity.User;

/**
 * 用户服务接口
 *
 * @author TestHub Team
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户ID
     */
    Long register(RegisterRequest request);

    /**
     * 根据ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
}
