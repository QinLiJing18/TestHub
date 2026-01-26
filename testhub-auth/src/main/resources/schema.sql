-- ========================================
-- TestHub Auth Service - 数据库表结构
-- 用户认证相关表
-- ========================================

-- 用户表
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
  id BIGINT NOT NULL AUTO_INCREMENT,                           -- 用户ID
  username VARCHAR(50) NOT NULL,                               -- 用户名
  password VARCHAR(255) NOT NULL,                              -- 密码(BCrypt加密)
  nickname VARCHAR(50) DEFAULT NULL,                           -- 昵称
  email VARCHAR(100) DEFAULT NULL,                             -- 邮箱
  phone VARCHAR(20) DEFAULT NULL,                              -- 手机号
  avatar VARCHAR(255) DEFAULT NULL,                            -- 头像URL
  roles VARCHAR(255) DEFAULT NULL,                             -- 角色列表(逗号分隔:ADMIN,TESTER)
  status TINYINT DEFAULT 1,                                    -- 状态(0-禁用 1-正常)
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 创建时间
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- 更新时间
  deleted TINYINT DEFAULT 0,                                   -- 逻辑删除(0-未删除 1-已删除)
  create_by BIGINT DEFAULT NULL,                               -- 创建人ID
  update_by BIGINT DEFAULT NULL,                               -- 更新人ID
  PRIMARY KEY (id)
);

-- 创建唯一索引
CREATE UNIQUE INDEX uk_username ON tb_user(username);
-- 创建普通索引
CREATE INDEX idx_email ON tb_user(email);
