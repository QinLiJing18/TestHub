package com.testhub.testcase.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.testhub.common.exception.BusinessException;
import com.testhub.testcase.entity.TestCase;
import com.testhub.testcase.mapper.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestCaseService {

    @Autowired
    private TestCaseMapper testCaseMapper;

    public List<TestCase> listCases(Long projectId) {
        LambdaQueryWrapper<TestCase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TestCase::getDeleted, 0);
        if (projectId != null) {
            wrapper.eq(TestCase::getProjectId, projectId);
        }
        wrapper.orderByDesc(TestCase::getCreateTime);
        return testCaseMapper.selectList(wrapper);
    }

    public TestCase getCaseById(Long id) {
        TestCase testCase = testCaseMapper.selectById(id);
        if (testCase == null || testCase.getDeleted() == 1) {
            throw new BusinessException(404, "测试用例不存在");
        }
        return testCase;
    }

    public Long createCase(TestCase testCase) {
        testCaseMapper.insert(testCase);
        return testCase.getId();
    }

    public void updateCase(TestCase testCase) {
        testCaseMapper.updateById(testCase);
    }

    public void deleteCase(Long id) {
        TestCase testCase = new TestCase();
        testCase.setId(id);
        testCase.setDeleted(1);
        testCaseMapper.updateById(testCase);
    }
}
