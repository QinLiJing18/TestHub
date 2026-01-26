package com.testhub.testcase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.testhub.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_testcase")
public class TestCase extends BaseEntity {
    private String caseNo;
    private String title;
    private Long deviceId;
    private Long projectId;
    private String testType;
    private String priority;
    private String preconditions;
    private String testSteps;  // JSON格式
    private String expectedResult;
    private String status;
}
