-- ========================================
-- TestHub Project Service - 初始化数据
-- ========================================

-- 插入测试项目
INSERT INTO tb_project (id, project_name, project_code, description, owner_id, status) VALUES
(1, '扫地机器人T1000测试项目', 'ROBOT_T1000', 'Anker扫地机器人T1000系列测试', 1, 'ACTIVE'),
(2, '空气净化器测试项目', 'AIR_PURIFIER_X1', '智能空气净化器X1系列测试', 1, 'ACTIVE');

-- 插入项目成员
INSERT INTO tb_project_member (project_id, user_id, role) VALUES
(1, 1, 'OWNER'),
(1, 2, 'MEMBER'),
(1, 3, 'MEMBER'),
(2, 1, 'OWNER');

-- 插入测试设备
INSERT INTO tb_device (id, device_name, device_type, device_sn, mqtt_client_id, project_id, status, description) VALUES
(1, '扫地机-T1000-001', 'ROBOT_VACUUM', 'T1000SN001', 'robot_t1000_001', 1, 'OFFLINE', 'T1000系列测试机1号'),
(2, '扫地机-T1000-002', 'ROBOT_VACUUM', 'T1000SN002', 'robot_t1000_002', 1, 'OFFLINE', 'T1000系列测试机2号'),
(3, '空气净化器-X1-001', 'AIR_PURIFIER', 'X1SN001', 'air_purifier_x1_001', 2, 'OFFLINE', 'X1系列测试机1号');
