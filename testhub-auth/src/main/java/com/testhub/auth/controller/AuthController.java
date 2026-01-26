package com.testhub.auth.controller;

import com.testhub.auth.dto.LoginRequest;
import com.testhub.auth.dto.LoginResponse;
import com.testhub.auth.dto.RegisterRequest;
import com.testhub.auth.entity.User;
import com.testhub.auth.service.UserService;
import com.testhub.common.dto.Result;
import com.testhub.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供登录、注册、用户信息查询等API
 *
 * @author TestHub Team
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户ID
     */
    @PostMapping("/register")
    public Result<Long> register(@RequestBody RegisterRequest request) {
        Long userId = userService.register(request);
        return Result.success("注册成功", userId);
    }

    /**
     * 获取当前用户信息
     * 从请求头中提取Token，解析用户ID，查询用户信息
     *
     * @param authorization Authorization请求头（格式: Bearer {token}）
     * @return 用户信息
     */
    @GetMapping("/user")
    public Result<User> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        // 1. 提取Token
        String token = authorization.replace("Bearer ", "");

        // 2. 解析Token获取用户ID
        Claims claims = JwtUtils.parseToken(token);
        Long userId = claims.get("userId", Long.class);

        // 3. 查询用户信息
        User user = userService.getUserById(userId);

        return Result.success(user);
    }

    /**
     * 退出登录
     * 前端清除Token即可，服务端无需处理
     *
     * @return 成功响应
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }
}
