-- ========================================
-- TestHub TestCase Service - 初始化数据
-- ========================================

-- 插入测试用例模板
INSERT INTO tb_testcase (id, case_no, title, device_id, project_id, test_type, priority, preconditions, test_steps, expected_result, status) VALUES
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
INSERT INTO tb_test_execution (id, execution_no, case_id, device_id, executor_id, status, result, start_time, end_time, duration) VALUES
(1, 'EXEC-20260114-001', 1, 1, 2, 'COMPLETED', 'PASS', TIMESTAMP '2026-01-14 10:00:00', TIMESTAMP '2026-01-14 10:02:30', 150),
(2, 'EXEC-20260114-002', 2, 1, 2, 'COMPLETED', 'PASS', TIMESTAMP '2026-01-14 10:05:00', TIMESTAMP '2026-01-14 10:15:00', 600),
(3, 'EXEC-20260114-003', 3, 1, 2, 'COMPLETED', 'FAIL', TIMESTAMP '2026-01-14 10:20:00', TIMESTAMP '2026-01-14 10:25:00', 300);
