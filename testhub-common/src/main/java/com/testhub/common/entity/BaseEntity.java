package com.testhub.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类（所有数据库实体类继承此类）
 *
 * 作用：统一管理公共字段（id、创建时间、更新时间、逻辑删除）
 *
 * 学习要点：
 * 1. @TableId：指定主键，IdType.AUTO表示数据库自增
 * 2. @TableField(fill = FieldFill.INSERT)：插入时自动填充
 * 3. @TableLogic：逻辑删除，删除时不物理删除，而是更新deleted字段
 *
 * @author TestHub Team
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间（插入时自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（插入和更新时自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识（0-未删除 1-已删除）
     * 使用@TableLogic后，删除操作会自动转换为UPDATE语句
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 创建人ID（可选字段，根据业务需要使用）
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人ID（可选字段，根据业务需要使用）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
}
