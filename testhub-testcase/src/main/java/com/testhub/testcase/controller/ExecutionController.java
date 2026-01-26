package com.testhub.testcase.controller;

import com.testhub.common.dto.Result;
import com.testhub.testcase.entity.TestExecution;
import com.testhub.testcase.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testcase")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @PostMapping("/execute/{caseId}")
    public Result<Long> executeCase(@PathVariable Long caseId, @RequestParam(required = false) Long executorId) {
        Long executionId = executionService.executeCase(caseId, executorId);
        return Result.success("执行成功", executionId);
    }

    @GetMapping("/executions")
    public Result<List<TestExecution>> listExecutions(@RequestParam(required = false) Long caseId) {
        List<TestExecution> executions = executionService.listExecutions(caseId);
        return Result.success(executions);
    }

    @GetMapping("/executions/{id}")
    public Result<TestExecution> getExecution(@PathVariable Long id) {
        TestExecution execution = executionService.getExecutionById(id);
        return Result.success(execution);
    }

    @GetMapping("/reports/{executionId}")
    public Result<Map<String, Object>> getReport(@PathVariable Long executionId) {
        TestExecution execution = executionService.getExecutionById(executionId);
        Map<String, Object> report = new HashMap<>();
        report.put("executionNo", execution.getExecutionNo());
        report.put("status", execution.getStatus());
        report.put("result", execution.getResult());
        report.put("duration", execution.getDuration());
        report.put("logContent", execution.getLogContent());
        return Result.success(report);
    }
}
