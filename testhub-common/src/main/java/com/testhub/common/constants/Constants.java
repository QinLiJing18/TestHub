package com.testhub.common.constants;

/**
 * 系统常量定义
 *
 * 作用：统一管理系统中使用的常量值，避免魔法数字
 *
 * @author TestHub Team
 */
public class Constants {

    // ==================== JWT相关常量 ====================

    /**
     * JWT密钥（实际生产环境应该从配置中心读取）
     */
    public static final String JWT_SECRET = "testHub_iot_2026_secret_key_for_jwt_token_generation";

    /**
     * JWT Token有效期（2小时，单位：毫秒）
     */
    public static final long JWT_EXPIRATION = 2 * 60 * 60 * 1000;

    /**
     * JWT Token请求头名称
     */
    public static final String JWT_HEADER = "Authorization";

    /**
     * JWT Token前缀
     */
    public static final String JWT_PREFIX = "Bearer ";

    // ==================== Redis Key前缀 ====================

    /**
     * 用户登录Token缓存前缀
     * 完整Key格式：login:token:user:123
     */
    public static final String REDIS_LOGIN_TOKEN_PREFIX = "login:token:user:";

    /**
     * 测试用例缓存前缀
     */
    public static final String REDIS_TESTCASE_PREFIX = "testcase:";

    /**
     * 设备在线状态缓存前缀
     */
    public static final String REDIS_DEVICE_ONLINE_PREFIX = "device:online:";

    // ==================== 角色权限常量 ====================

    /**
     * 角色：系统管理员
     */
    public static final String ROLE_ADMIN = "ADMIN";

    /**
     * 角色：测试负责人
     */
    public static final String ROLE_TEST_MANAGER = "TEST_MANAGER";

    /**
     * 角色：测试工程师
     */
    public static final String ROLE_TESTER = "TESTER";

    /**
     * 角色：访客
     */
    public static final String ROLE_VIEWER = "VIEWER";

    // ==================== 设备类型常量 ====================

    /**
     * 设备类型：扫地机器人
     */
    public static final String DEVICE_TYPE_ROBOT_VACUUM = "ROBOT_VACUUM";

    /**
     * 设备类型：空气净化器
     */
    public static final String DEVICE_TYPE_AIR_PURIFIER = "AIR_PURIFIER";

    /**
     * 设备类型：智能音箱
     */
    public static final String DEVICE_TYPE_SMART_SPEAKER = "SMART_SPEAKER";

    // ==================== 测试用例类型常量 ====================

    /**
     * 测试类型：冒烟测试
     */
    public static final String TEST_TYPE_SMOKE = "SMOKE";

    /**
     * 测试类型：功能测试
     */
    public static final String TEST_TYPE_FUNCTIONAL = "FUNCTIONAL";

    /**
     * 测试类型：性能测试
     */
    public static final String TEST_TYPE_PERFORMANCE = "PERFORMANCE";

    /**
     * 测试类型：专项测试
     */
    public static final String TEST_TYPE_SPECIALIZED = "SPECIALIZED";

    // ==================== 测试执行状态常量 ====================

    /**
     * 执行状态：待执行
     */
    public static final String EXECUTION_STATUS_PENDING = "PENDING";

    /**
     * 执行状态：执行中
     */
    public static final String EXECUTION_STATUS_RUNNING = "RUNNING";

    /**
     * 执行状态：已完成
     */
    public static final String EXECUTION_STATUS_COMPLETED = "COMPLETED";

    /**
     * 执行状态：失败
     */
    public static final String EXECUTION_STATUS_FAILED = "FAILED";

    // ==================== MQTT主题常量 ====================

    /**
     * MQTT主题：设备控制指令
     * 格式：device/command/{deviceId}
     */
    public static final String MQTT_TOPIC_DEVICE_COMMAND = "device/command/";

    /**
     * MQTT主题：设备状态上报
     * 格式：device/status/{deviceId}
     */
    public static final String MQTT_TOPIC_DEVICE_STATUS = "device/status/";

    /**
     * MQTT主题：测试日志上报
     * 格式：test/log/{executionId}
     */
    public static final String MQTT_TOPIC_TEST_LOG = "test/log/";

    // ==================== 通用常量 ====================

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 失败状态码
     */
    public static final int FAIL_CODE = 400;

    /**
     * 未认证状态码
     */
    public static final int UNAUTHORIZED_CODE = 401;

    /**
     * 无权限状态码
     */
    public static final int FORBIDDEN_CODE = 403;

    /**
     * 服务器错误状态码
     */
    public static final int ERROR_CODE = 500;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
}
