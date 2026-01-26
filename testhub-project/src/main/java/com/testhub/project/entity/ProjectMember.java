package com.testhub.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.testhub.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_project_member")
public class ProjectMember extends BaseEntity {
    private Long projectId;
    private Long userId;
    private String role;  // OWNER, MEMBER, VIEWER
    private LocalDateTime joinTime;
}
