-- ========================================
-- TestHub Auth Service - 初始化数据
-- ========================================

-- 插入测试用户
-- 密码均为: admin123 (已BCrypt加密, strength=10)
-- BCrypt hash verified working: $2a$10$jRcoeUkntFuJPAcTuNNnU.VYU/CXMZNN/FnDwIb25X./Uqmjq1DwC
INSERT INTO tb_user (id, username, password, nickname, email, roles, status) VALUES
(1, 'admin', '$2a$10$jRcoeUkntFuJPAcTuNNnU.VYU/CXMZNN/FnDwIb25X./Uqmjq1DwC', '系统管理员', 'admin@testhub.com', 'ADMIN,TEST_MANAGER', 1),
(2, 'tester1', '$2a$10$jRcoeUkntFuJPAcTuNNnU.VYU/CXMZNN/FnDwIb25X./Uqmjq1DwC', '测试工程师1', 'tester1@testhub.com', 'TESTER', 1),
(3, 'tester2', '$2a$10$jRcoeUkntFuJPAcTuNNnU.VYU/CXMZNN/FnDwIb25X./Uqmjq1DwC', '测试工程师2', 'tester2@testhub.com', 'TESTER', 1);
