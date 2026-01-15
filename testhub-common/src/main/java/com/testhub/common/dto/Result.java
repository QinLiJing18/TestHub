package com.testhub.common.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果封装类
 *
 * 作用：所有Controller接口返回此格式，前端统一解析
 *
 * 学习要点：
 * 1. 使用泛型<T>支持任意返回数据类型
 * 2. 提供静态方法快速构建成功/失败响应
 * 3. 符合RESTful API设计规范
 *
 * @author TestHub Team
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     * 200: 成功
     * 400: 参数错误
     * 401: 未认证
     * 403: 无权限
     * 500: 服务器错误
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据（泛型，可以是任意类型）
     */
    private T data;

    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;

    // ==================== 构造方法 ====================

    /**
     * 私有构造方法，强制使用静态工厂方法创建对象
     */
    private Result() {
        this.timestamp = LocalDateTime.now();
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // ==================== 静态工厂方法（推荐使用） ====================

    /**
     * 成功响应（无数据）
     *
     * 使用场景：删除、更新等不需要返回数据的操作
     * 示例：return Result.success();
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     *
     * 使用场景：查询操作
     * 示例：return Result.success(user);
     *
     * @param data 返回的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功响应（带自定义消息和数据）
     *
     * 使用场景：需要自定义成功提示的场景
     * 示例：return Result.success("登录成功", token);
     *
     * @param message 自定义消息
     * @param data 返回的数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败响应（带消息）
     *
     * 使用场景：业务逻辑错误
     * 示例：return Result.fail("用户名已存在");
     *
     * @param message 错误消息
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(400, message, null);
    }

    /**
     * 失败响应（带状态码和消息）
     *
     * 使用场景：需要指定特定错误码
     * 示例：return Result.fail(401, "未登录");
     *
     * @param code 状态码
     * @param message 错误消息
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 未认证响应
     *
     * 使用场景：Token无效或已过期
     * 示例：return Result.unauthorized();
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未登录或登录已过期", null);
    }

    /**
     * 无权限响应
     *
     * 使用场景：当前用户无操作权限
     * 示例：return Result.forbidden();
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "无权限访问", null);
    }

    /**
     * 服务器错误响应
     *
     * 使用场景：系统异常
     * 示例：return Result.error();
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "服务器内部错误", null);
    }

    /**
     * 服务器错误响应（带消息）
     *
     * 使用场景：系统异常需要返回具体错误信息
     * 示例：return Result.error("数据库连接失败");
     *
     * @param message 错误消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // ==================== 判断方法 ====================

    /**
     * 判断是否成功
     *
     * @return true-成功 false-失败
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}
