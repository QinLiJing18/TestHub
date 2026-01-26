package com.testhub.testcase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.testhub.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_test_execution")
public class TestExecution extends BaseEntity {
    private String executionNo;
    private Long caseId;
    private Long deviceId;
    private Long executorId;
    private String status;  // PENDING, RUNNING, COMPLETED, FAILED
    private String result;  // PASS, FAIL, BLOCKED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private String logContent;
    private String errorMessage;
}
