package com.testhub.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.testhub.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_project")
public class Project extends BaseEntity {
    private String projectName;
    private String projectCode;
    private String description;
    private Long ownerId;
    private String status;  // ACTIVE, FINISHED, ARCHIVED
}
