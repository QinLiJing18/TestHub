-- ========================================
-- TestHub IoT测试管理平台 - 数据库初始化脚本
-- 版本: 1.0.0
-- 作者: TestHub Team
-- 说明: 创建数据库、表结构和初始化数据
-- ========================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS `testhub` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `testhub`;

-- ========================================
-- 用户认证相关表
-- ========================================

-- 2. 用户表
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `roles` VARCHAR(255) DEFAULT NULL COMMENT '角色列表(逗号分隔:ADMIN,TESTER)',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0-禁用 1-正常)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0-未删除 1-已删除)',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========================================
-- 项目管理相关表
-- ========================================

-- 3. 项目表
DROP TABLE IF EXISTS `tb_project`;
CREATE TABLE `tb_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `project_code` VARCHAR(50) NOT NULL COMMENT '项目编码',
  `description` TEXT COMMENT '项目描述',
  `owner_id` BIGINT NOT NULL COMMENT '负责人ID',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE-进行中,FINISHED-已完成,ARCHIVED-已归档)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  `create_by` BIGINT DEFAULT NULL,
  `update_by` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_code` (`project_code`),
  KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 4. 项目成员表
DROP TABLE IF EXISTS `tb_project_member`;
CREATE TABLE `tb_project_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成员ID',
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role` VARCHAR(20) DEFAULT 'MEMBER' COMMENT '角色(OWNER-负责人,MEMBER-成员,VIEWER-访客)',
  `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员表';

-- ========================================
-- IoT设备相关表
-- ========================================

-- 5. 设备表
DROP TABLE IF EXISTS `tb_device`;
CREATE TABLE `tb_device` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `device_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
  `device_type` VARCHAR(50) NOT NULL COMMENT '设备类型(ROBOT_VACUUM-扫地机,AIR_PURIFIER-空气净化器)',
  `device_sn` VARCHAR(100) DEFAULT NULL COMMENT '设备序列号',
  `mqtt_client_id` VARCHAR(100) NOT NULL COMMENT 'MQTT客户端ID',
  `mqtt_username` VARCHAR(100) DEFAULT NULL COMMENT 'MQTT用户名',
  `mqtt_password` VARCHAR(100) DEFAULT NULL COMMENT 'MQTT密码',
  `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
  `status` VARCHAR(20) DEFAULT 'OFFLINE' COMMENT '状态(ONLINE-在线,OFFLINE-离线,TESTING-测试中)',
  `last_online_time` DATETIME DEFAULT NULL COMMENT '最后在线时间',
  `description` TEXT COMMENT '设备描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  `create_by` BIGINT DEFAULT NULL,
  `update_by` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mqtt_client_id` (`mqtt_client_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_device_type` (`device_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT设备表';

-- ========================================
-- 测试用例相关表
-- ========================================

-- 6. 测试用例表
DROP TABLE IF EXISTS `tb_testcase`;
CREATE TABLE `tb_testcase` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用例ID',
  `case_no` VARCHAR(50) NOT NULL COMMENT '用例编号',
  `title` VARCHAR(200) NOT NULL COMMENT '用例标题',
  `device_id` BIGINT DEFAULT NULL COMMENT '关联设备ID',
  `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
  `test_type` VARCHAR(50) DEFAULT NULL COMMENT '测试类型(SMOKE-冒烟,FUNCTIONAL-功能,PERFORMANCE-性能)',
  `priority` VARCHAR(10) DEFAULT 'P2' COMMENT '优先级(P0-最高,P1-高,P2-中,P3-低)',
  `preconditions` TEXT COMMENT '前置条件',
  `test_steps` JSON COMMENT '测试步骤JSON',
  `expected_result` TEXT COMMENT '预期结果',
  `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态(DRAFT-草稿,ACTIVE-激活,DEPRECATED-废弃)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  `create_by` BIGINT DEFAULT NULL,
  `update_by` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_case_no` (`case_no`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用例表';

-- 7. 测试执行表
DROP TABLE IF EXISTS `tb_test_execution`;
CREATE TABLE `tb_test_execution` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '执行ID',
  `execution_no` VARCHAR(50) NOT NULL COMMENT '执行编号',
  `case_id` BIGINT NOT NULL COMMENT '用例ID',
  `device_id` BIGINT DEFAULT NULL COMMENT '执行设备ID',
  `executor_id` BIGINT NOT NULL COMMENT '执行人ID',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态(PENDING-待执行,RUNNING-执行中,COMPLETED-已完成,FAILED-失败)',
  `result` VARCHAR(20) DEFAULT NULL COMMENT '结果(PASS-通过,FAIL-失败,BLOCKED-阻塞)',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `duration` INT DEFAULT NULL COMMENT '执行时长(秒)',
  `log_content` LONGTEXT COMMENT '执行日志',
  `error_message` TEXT COMMENT '错误信息',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_execution_no` (`execution_no`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_executor_id` (`executor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试执行表';

-- 8. 测试报告表
DROP TABLE IF EXISTS `tb_test_report`;
CREATE TABLE `tb_test_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `report_no` VARCHAR(50) NOT NULL COMMENT '报告编号',
  `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
  `report_name` VARCHAR(200) NOT NULL COMMENT '报告名称',
  `report_type` VARCHAR(50) DEFAULT 'EXECUTION' COMMENT '报告类型(EXECUTION-执行报告,SUMMARY-汇总报告)',
  `total_cases` INT DEFAULT 0 COMMENT '总用例数',
  `passed_cases` INT DEFAULT 0 COMMENT '通过用例数',
  `failed_cases` INT DEFAULT 0 COMMENT '失败用例数',
  `blocked_cases` INT DEFAULT 0 COMMENT '阻塞用例数',
  `pass_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '通过率(%)',
  `report_file_url` VARCHAR(255) DEFAULT NULL COMMENT '报告文件URL',
  `start_time` DATETIME DEFAULT NULL COMMENT '测试开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '测试结束时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  `create_by` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_no` (`report_no`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试报告表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入测试用户(密码均为: admin123, 已BCrypt加密)
INSERT INTO `tb_user` (`id`, `username`, `password`, `nickname`, `email`, `roles`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@testhub.com', 'ADMIN,TEST_MANAGER', 1),
(2, 'tester1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试工程师1', 'tester1@testhub.com', 'TESTER', 1),
(3, 'tester2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试工程师2', 'tester2@testhub.com', 'TESTER', 1);

-- 插入测试项目
INSERT INTO `tb_project` (`id`, `project_name`, `project_code`, `description`, `owner_id`, `status`) VALUES
(1, '扫地机器人T1000测试项目', 'ROBOT_T1000', 'Anker扫地机器人T1000系列测试', 1, 'ACTIVE'),
(2, '空气净化器测试项目', 'AIR_PURIFIER_X1', '智能空气净化器X1系列测试', 1, 'ACTIVE');

-- 插入项目成员
INSERT INTO `tb_project_member` (`project_id`, `user_id`, `role`) VALUES
(1, 1, 'OWNER'),
(1, 2, 'MEMBER'),
(1, 3, 'MEMBER'),
(2, 1, 'OWNER');

-- 插入测试设备
INSERT INTO `tb_device` (`id`, `device_name`, `device_type`, `device_sn`, `mqtt_client_id`, `project_id`, `status`, `description`) VALUES
(1, '扫地机-T1000-001', 'ROBOT_VACUUM', 'T1000SN001', 'robot_t1000_001', 1, 'OFFLINE', 'T1000系列测试机1号'),
(2, '扫地机-T1000-002', 'ROBOT_VACUUM', 'T1000SN002', 'robot_t1000_002', 1, 'OFFLINE', 'T1000系列测试机2号'),
(3, '空气净化器-X1-001', 'AIR_PURIFIER', 'X1SN001', 'air_purifier_x1_001', 2, 'OFFLINE', 'X1系列测试机1号');

-- 插入测试用例模板
INSERT INTO `tb_testcase` (`id`, `case_no`, `title`, `device_id`, `project_id`, `test_type`, `priority`, `preconditions`, `test_steps`, `expected_result`, `status`) VALUES
(1, 'TC-ROBOT-001', '扫地机-开机测试', 1, 1, 'SMOKE', 'P0', '设备已充电,设备处于关机状态',
'[{"step":1,"action":"按下电源键","expected":"听到开机提示音"},{"step":2,"action":"观察设备状态","expected":"LED灯亮起"},{"step":3,"action":"检查MQTT连接","expected":"设备上线"}]',
'设备成功开机,所有指示灯正常,MQTT连接建立', 'ACTIVE'),

(2, 'TC-ROBOT-002', '扫地机-启动清扫测试', 1, 1, 'FUNCTIONAL', 'P1', '设备已开机,设备处于待机状态',
'[{"step":1,"action":"点击开始清扫按钮","expected":"设备开始移动"},{"step":2,"action":"观察清扫路径","expected":"设备按规划路径清扫"},{"step":3,"action":"监控清扫数据","expected":"实时上报清扫面积"}]',
'设备正常启动清扫,路径规划合理,数据上报正常', 'ACTIVE'),

(3, 'TC-ROBOT-003', '扫地机-返回充电测试', 1, 1, 'FUNCTIONAL', 'P1', '设备正在清扫中,电量低于20%',
'[{"step":1,"action":"触发低电量告警","expected":"设备提示低电量"},{"step":2,"action":"观察设备行为","expected":"自动返回充电桩"},{"step":3,"action":"检查充电状态","expected":"开始充电"}]',
'设备正确识别低电量,自动返回充电桩并开始充电', 'ACTIVE');

-- 插入测试执行记录(示例)
INSERT INTO `tb_test_execution` (`id`, `execution_no`, `case_id`, `device_id`, `executor_id`, `status`, `result`, `start_time`, `end_time`, `duration`) VALUES
(1, 'EXEC-20260114-001', 1, 1, 2, 'COMPLETED', 'PASS', '2026-01-14 10:00:00', '2026-01-14 10:02:30', 150),
(2, 'EXEC-20260114-002', 2, 1, 2, 'COMPLETED', 'PASS', '2026-01-14 10:05:00', '2026-01-14 10:15:00', 600),
(3, 'EXEC-20260114-003', 3, 1, 2, 'COMPLETED', 'FAIL', '2026-01-14 10:20:00', '2026-01-14 10:25:00', 300);

-- ========================================
-- 完成
-- ========================================
SELECT '数据库初始化完成！' AS message;
SELECT '默认管理员账号: admin / admin123' AS info;
