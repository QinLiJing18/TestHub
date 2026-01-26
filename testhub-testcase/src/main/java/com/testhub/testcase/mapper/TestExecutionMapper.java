package com.testhub.testcase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.testhub.testcase.entity.TestExecution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestExecutionMapper extends BaseMapper<TestExecution> {
}
