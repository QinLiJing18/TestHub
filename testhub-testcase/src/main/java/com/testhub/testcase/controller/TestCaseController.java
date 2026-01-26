package com.testhub.testcase.controller;

import com.testhub.common.dto.Result;
import com.testhub.testcase.entity.TestCase;
import com.testhub.testcase.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/testcase")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    @GetMapping("/cases")
    public Result<List<TestCase>> listCases(@RequestParam(required = false) Long projectId) {
        List<TestCase> cases = testCaseService.listCases(projectId);
        return Result.success(cases);
    }

    @GetMapping("/cases/{id}")
    public Result<TestCase> getCase(@PathVariable Long id) {
        TestCase testCase = testCaseService.getCaseById(id);
        return Result.success(testCase);
    }

    @PostMapping("/cases")
    public Result<Long> createCase(@RequestBody TestCase testCase) {
        Long id = testCaseService.createCase(testCase);
        return Result.success("创建成功", id);
    }

    @PutMapping("/cases/{id}")
    public Result<String> updateCase(@PathVariable Long id, @RequestBody TestCase testCase) {
        testCase.setId(id);
        testCaseService.updateCase(testCase);
        return Result.success("更新成功");
    }

    @DeleteMapping("/cases/{id}")
    public Result<String> deleteCase(@PathVariable Long id) {
        testCaseService.deleteCase(id);
        return Result.success("删除成功");
    }
}
