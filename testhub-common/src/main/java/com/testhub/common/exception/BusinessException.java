package com.testhub.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类
 *
 * 作用：业务逻辑中抛出的异常（如用户名已存在、设备不存在等）
 *
 * 学习要点：
 * 1. 继承RuntimeException，属于非受检异常，无需强制捕获
 * 2. 携带错误码和错误消息，便于统一异常处理
 * 3. 在GlobalExceptionHandler中统一捕获并转换为Result响应
 *
 * 使用示例：
 * if (user == null) {
 *     throw new BusinessException(404, "用户不存在");
 * }
 *
 * @author TestHub Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    // ==================== 构造方法 ====================

    /**
     * 默认构造方法（400错误码）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }

    /**
     * 自定义错误码和消息
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 带原始异常的构造方法（用于包装其他异常）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
