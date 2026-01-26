-- ========================================
-- TestHub TestCase Service - 数据库表结构
-- 测试用例、执行记录、测试报告相关表
-- ========================================

-- 测试用例表
DROP TABLE IF EXISTS tb_testcase;
CREATE TABLE tb_testcase (
  id BIGINT NOT NULL AUTO_INCREMENT,                           -- 用例ID
  case_no VARCHAR(50) NOT NULL,                                -- 用例编号
  title VARCHAR(200) NOT NULL,                                 -- 用例标题
  device_id BIGINT DEFAULT NULL,                               -- 关联设备ID
  project_id BIGINT NOT NULL,                                  -- 所属项目ID
  test_type VARCHAR(50) DEFAULT NULL,                          -- 测试类型(SMOKE-冒烟,FUNCTIONAL-功能,PERFORMANCE-性能)
  priority VARCHAR(10) DEFAULT 'P2',                           -- 优先级(P0-最高,P1-高,P2-中,P3-低)
  preconditions TEXT,                                          -- 前置条件
  test_steps VARCHAR(4000),                                    -- 测试步骤JSON(H2不支持JSON类型,使用VARCHAR)
  expected_result TEXT,                                        -- 预期结果
  status VARCHAR(20) DEFAULT 'DRAFT',                          -- 状态(DRAFT-草稿,ACTIVE-激活,DEPRECATED-废弃)
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 创建时间
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 更新时间
  deleted TINYINT DEFAULT 0,                                   -- 逻辑删除
  create_by BIGINT DEFAULT NULL,                               -- 创建人ID
  update_by BIGINT DEFAULT NULL,                               -- 更新人ID
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_case_no ON tb_testcase(case_no);
CREATE INDEX idx_project_id ON tb_testcase(project_id);
CREATE INDEX idx_device_id ON tb_testcase(device_id);

-- 测试执行表
DROP TABLE IF EXISTS tb_test_execution;
CREATE TABLE tb_test_execution (
  id BIGINT NOT NULL AUTO_INCREMENT,                           -- 执行ID
  execution_no VARCHAR(50) NOT NULL,                           -- 执行编号
  case_id BIGINT NOT NULL,                                     -- 用例ID
  device_id BIGINT DEFAULT NULL,                               -- 执行设备ID
  executor_id BIGINT NOT NULL,                                 -- 执行人ID
  status VARCHAR(20) DEFAULT 'PENDING',                        -- 状态(PENDING-待执行,RUNNING-执行中,COMPLETED-已完成,FAILED-失败)
  result VARCHAR(20) DEFAULT NULL,                             -- 结果(PASS-通过,FAIL-失败,BLOCKED-阻塞)
  start_time TIMESTAMP DEFAULT NULL,                           -- 开始时间
  end_time TIMESTAMP DEFAULT NULL,                             -- 结束时间
  duration INT DEFAULT NULL,                                   -- 执行时长(秒)
  log_content CLOB,                                            -- 执行日志(H2使用CLOB代替LONGTEXT)
  error_message TEXT,                                          -- 错误信息
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 创建时间
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 更新时间
  deleted TINYINT DEFAULT 0,                                   -- 逻辑删除
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_execution_no ON tb_test_execution(execution_no);
CREATE INDEX idx_case_id ON tb_test_execution(case_id);
CREATE INDEX idx_executor_id ON tb_test_execution(executor_id);

-- 测试报告表
DROP TABLE IF EXISTS tb_test_report;
CREATE TABLE tb_test_report (
  id BIGINT NOT NULL AUTO_INCREMENT,                           -- 报告ID
  report_no VARCHAR(50) NOT NULL,                              -- 报告编号
  project_id BIGINT NOT NULL,                                  -- 所属项目ID
  report_name VARCHAR(200) NOT NULL,                           -- 报告名称
  report_type VARCHAR(50) DEFAULT 'EXECUTION',                 -- 报告类型(EXECUTION-执行报告,SUMMARY-汇总报告)
  total_cases INT DEFAULT 0,                                   -- 总用例数
  passed_cases INT DEFAULT 0,                                  -- 通过用例数
  failed_cases INT DEFAULT 0,                                  -- 失败用例数
  blocked_cases INT DEFAULT 0,                                 -- 阻塞用例数
  pass_rate DECIMAL(5,2) DEFAULT 0.00,                         -- 通过率(%)
  report_file_url VARCHAR(255) DEFAULT NULL,                   -- 报告文件URL
  start_time TIMESTAMP DEFAULT NULL,                           -- 测试开始时间
  end_time TIMESTAMP DEFAULT NULL,                             -- 测试结束时间
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 创建时间
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 更新时间
  deleted TINYINT DEFAULT 0,                                   -- 逻辑删除
  create_by BIGINT DEFAULT NULL,                               -- 创建人ID
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_report_no ON tb_test_report(report_no);
CREATE INDEX idx_project_id ON tb_test_report(project_id);
