-- ========================================
-- TestHub Project Service - 数据库表结构
-- 项目管理和IoT设备相关表
-- ========================================

-- 项目表
DROP TABLE IF EXISTS tb_project;
CREATE TABLE tb_project (
  id BIGINT NOT NULL AUTO_INCREMENT,                           -- 项目ID
  project_name VARCHAR(100) NOT NULL,                          -- 项目名称
  project_code VARCHAR(50) NOT NULL,                           -- 项目编码
  description TEXT,                                            -- 项目描述
  owner_id BIGINT NOT NULL,                                    -- 负责人ID
  status VARCHAR(20) DEFAULT 'ACTIVE',                         -- 状态(ACTIVE-进行中,FINISHED-已完成,ARCHIVED-已归档)
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 创建时间
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 更新时间
  deleted TINYINT DEFAULT 0,                                   -- 逻辑删除
  create_by BIGINT DEFAULT NULL,                               -- 创建人ID
  update_by BIGINT DEFAULT NULL,                               -- 更新人ID
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_project_code ON tb_project(project_code);
CREATE INDEX idx_owner_id ON tb_project(owner_id);

-- 项目成员表
DROP TABLE IF EXISTS tb_project_member;
CREATE TABLE tb_project_member (
  id BIGINT NOT NULL AUTO_INCREMENT,                           -- 成员ID
  project_id BIGINT NOT NULL,                                  -- 项目ID
  user_id BIGINT NOT NULL,                                     -- 用户ID
  role VARCHAR(20) DEFAULT 'MEMBER',                           -- 角色(OWNER-负责人,MEMBER-成员,VIEWER-访客)
  join_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,               -- 加入时间
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 创建时间
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 更新时间
  deleted TINYINT DEFAULT 0,                                   -- 逻辑删除
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_project_user ON tb_project_member(project_id, user_id);

-- IoT设备表
DROP TABLE IF EXISTS tb_device;
CREATE TABLE tb_device (
  id BIGINT NOT NULL AUTO_INCREMENT,                           -- 设备ID
  device_name VARCHAR(100) NOT NULL,                           -- 设备名称
  device_type VARCHAR(50) NOT NULL,                            -- 设备类型(ROBOT_VACUUM-扫地机,AIR_PURIFIER-空气净化器)
  device_sn VARCHAR(100) DEFAULT NULL,                         -- 设备序列号
  mqtt_client_id VARCHAR(100) NOT NULL,                        -- MQTT客户端ID
  mqtt_username VARCHAR(100) DEFAULT NULL,                     -- MQTT用户名
  mqtt_password VARCHAR(100) DEFAULT NULL,                     -- MQTT密码
  project_id BIGINT NOT NULL,                                  -- 所属项目ID
  status VARCHAR(20) DEFAULT 'OFFLINE',                        -- 状态(ONLINE-在线,OFFLINE-离线,TESTING-测试中)
  last_online_time TIMESTAMP DEFAULT NULL,                     -- 最后在线时间
  description TEXT,                                            -- 设备描述
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 创建时间
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 更新时间
  deleted TINYINT DEFAULT 0,                                   -- 逻辑删除
  create_by BIGINT DEFAULT NULL,                               -- 创建人ID
  update_by BIGINT DEFAULT NULL,                               -- 更新人ID
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_mqtt_client_id ON tb_device(mqtt_client_id);
CREATE INDEX idx_project_id ON tb_device(project_id);
CREATE INDEX idx_device_type ON tb_device(device_type);
