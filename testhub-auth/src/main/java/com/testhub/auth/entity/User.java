package com.testhub.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.testhub.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 *
 * @author TestHub Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_user")
public class User extends BaseEntity {

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 角色列表（逗号分隔，如：ADMIN,TESTER）
     */
    private String roles;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;
}
