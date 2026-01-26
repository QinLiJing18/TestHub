package com.testhub.testcase.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.testhub.common.exception.BusinessException;
import com.testhub.testcase.entity.TestCase;
import com.testhub.testcase.entity.TestExecution;
import com.testhub.testcase.mapper.TestCaseMapper;
import com.testhub.testcase.mapper.TestExecutionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class ExecutionService {

    @Autowired
    private TestExecutionMapper executionMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    public Long executeCase(Long caseId, Long executorId) {
        // 1. 查询用例
        TestCase testCase = testCaseMapper.selectById(caseId);
        if (testCase == null) {
            throw new BusinessException(404, "测试用例不存在");
        }

        // 2. 创建执行记录
        TestExecution execution = new TestExecution();
        execution.setExecutionNo("EXEC-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        execution.setCaseId(caseId);
        execution.setDeviceId(testCase.getDeviceId());
        execution.setExecutorId(executorId != null ? executorId : 1L);
        execution.setStatus("RUNNING");
        execution.setStartTime(LocalDateTime.now());

        executionMapper.insert(execution);

        // 3. 模拟执行（实际应该异步调用MQTT）
        try {
            Thread.sleep(2000);  // 模拟执行2秒

            // 随机生成结果（70%成功）
            boolean pass = new Random().nextInt(100) < 70;

            execution.setStatus("COMPLETED");
            execution.setResult(pass ? "PASS" : "FAIL");
            execution.setEndTime(LocalDateTime.now());
            execution.setDuration(2);
            execution.setLogContent("测试执行完成\n步骤1: 通过\n步骤2: 通过\n步骤3: " + (pass ? "通过" : "失败"));
            if (!pass) {
                execution.setErrorMessage("第3步执行失败");
            }

            executionMapper.updateById(execution);
        } catch (Exception e) {
            execution.setStatus("FAILED");
            execution.setResult("FAIL");
            execution.setErrorMessage(e.getMessage());
            executionMapper.updateById(execution);
        }

        return execution.getId();
    }

    public List<TestExecution> listExecutions(Long caseId) {
        LambdaQueryWrapper<TestExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TestExecution::getDeleted, 0);
        if (caseId != null) {
            wrapper.eq(TestExecution::getCaseId, caseId);
        }
        wrapper.orderByDesc(TestExecution::getCreateTime);
        return executionMapper.selectList(wrapper);
    }

    public TestExecution getExecutionById(Long id) {
        TestExecution execution = executionMapper.selectById(id);
        if (execution == null || execution.getDeleted() == 1) {
            throw new BusinessException(404, "执行记录不存在");
        }
        return execution;
    }
}
