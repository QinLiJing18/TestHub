package com.testhub.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.testhub.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_device")
public class Device extends BaseEntity {
    private String deviceName;
    private String deviceType;
    private String deviceSn;
    private String mqttClientId;
    private String mqttUsername;
    private String mqttPassword;
    private Long projectId;
    private String status;  // ONLINE, OFFLINE, TESTING
    private LocalDateTime lastOnlineTime;
    private String description;
}
