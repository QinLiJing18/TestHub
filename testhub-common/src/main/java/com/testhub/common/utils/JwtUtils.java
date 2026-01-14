package com.testhub.common.utils;

import cn.hutool.core.date.DateUtil;
import com.testhub.common.constants.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * 作用：生成和解析JWT Token，用于用户认证
 *
 * 学习要点：
 * 1. JWT由三部分组成：Header（头部）、Payload（负载）、Signature（签名）
 * 2. 使用HMAC-SHA256算法签名，确保Token不被篡改
 * 3. Token包含用户信息（如userId、username），无需查询数据库即可识别用户
 * 4. Token有过期时间，过期后需重新登录
 *
 * JWT结构示例：
 * eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4ifQ.xxxxx
 * |------- Header -------|------- Payload --------|-- Signature --|
 *
 * @author TestHub Team
 */
@Slf4j
public class JwtUtils {

    /**
     * 生成签名密钥
     * 学习要点：使用Keys.hmacShaKeyFor()生成符合规范的密钥
     */
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Constants.JWT_SECRET.getBytes());

    // ==================== 生成Token ====================

    /**
     * 生成JWT Token
     *
     * 输入：用户信息（Map格式，如{"userId": 1, "username": "admin"}）
     * 输出：JWT Token字符串
     *
     * 使用示例：
     * Map<String, Object> claims = new HashMap<>();
     * claims.put("userId", 1L);
     * claims.put("username", "admin");
     * String token = JwtUtils.generateToken(claims);
     *
     * @param claims 用户信息（存储在Token中的数据）
     * @return JWT Token字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        // 当前时间
        Date now = new Date();
        // 过期时间（当前时间 + 2小时）
        Date expireTime = DateUtil.offsetMillisecond(now, (int) Constants.JWT_EXPIRATION);

        // 构建JWT Token
        return Jwts.builder()
                // 设置自定义声明（用户信息）
                .claims(claims)
                // 设置签发时间
                .issuedAt(now)
                // 设置过期时间
                .expiration(expireTime)
                // 使用HMAC-SHA256算法签名
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                // 生成最终的Token字符串
                .compact();
    }

    // ==================== 解析Token ====================

    /**
     * 解析JWT Token（获取其中的用户信息）
     *
     * 输入：JWT Token字符串
     * 输出：Token中存储的用户信息（Claims对象）
     *
     * 使用示例：
     * Claims claims = JwtUtils.parseToken(token);
     * Long userId = claims.get("userId", Long.class);
     * String username = claims.get("username", String.class);
     *
     * @param token JWT Token字符串
     * @return Claims对象（包含用户信息）
     * @throws JwtException Token无效或已过期
     */
    public static Claims parseToken(String token) {
        try {
            // 解析Token并验证签名
            return Jwts.parser()
                    // 设置签名密钥
                    .verifyWith(SECRET_KEY)
                    .build()
                    // 解析Token
                    .parseSignedClaims(token)
                    // 获取Payload部分
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // Token已过期
            log.error("JWT Token已过期: {}", e.getMessage());
            throw new JwtException("Token已过期，请重新登录");
        } catch (UnsupportedJwtException e) {
            // 不支持的Token格式
            log.error("不支持的JWT Token: {}", e.getMessage());
            throw new JwtException("Token格式错误");
        } catch (MalformedJwtException e) {
            // Token格式错误
            log.error("JWT Token格式错误: {}", e.getMessage());
            throw new JwtException("Token格式错误");
        } catch (SecurityException | IllegalArgumentException e) {
            // 签名验证失败
            log.error("JWT Token签名验证失败: {}", e.getMessage());
            throw new JwtException("Token无效");
        }
    }

    // ==================== 验证Token ====================

    /**
     * 验证JWT Token是否有效
     *
     * 输入：JWT Token字符串
     * 输出：true-有效 false-无效
     *
     * 使用示例：
     * if (JwtUtils.validateToken(token)) {
     *     // Token有效，允许访问
     * } else {
     *     // Token无效，拒绝访问
     * }
     *
     * @param token JWT Token字符串
     * @return true-有效 false-无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // ==================== 获取Token中的用户ID ====================

    /**
     * 从Token中获取用户ID
     *
     * 输入：JWT Token字符串
     * 输出：用户ID
     *
     * 使用示例：
     * Long userId = JwtUtils.getUserIdFromToken(token);
     *
     * @param token JWT Token字符串
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取用户名
     *
     * 输入：JWT Token字符串
     * 输出：用户名
     *
     * @param token JWT Token字符串
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    // ==================== 检查Token是否即将过期 ====================

    /**
     * 检查Token是否即将过期（剩余有效期<30分钟）
     *
     * 输入：JWT Token字符串
     * 输出：true-即将过期 false-未即将过期
     *
     * 应用场景：可以在Token即将过期时主动刷新Token，提升用户体验
     *
     * @param token JWT Token字符串
     * @return true-即将过期 false-未即将过期
     */
    public static boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            // 计算剩余有效期（毫秒）
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            // 小于30分钟（1800000毫秒）则认为即将过期
            return remainingTime < 30 * 60 * 1000;
        } catch (JwtException e) {
            return true;
        }
    }
}
