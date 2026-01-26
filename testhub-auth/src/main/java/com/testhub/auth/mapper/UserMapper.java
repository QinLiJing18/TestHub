package com.testhub.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.testhub.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 * 继承BaseMapper后自动获得单表CRUD方法
 *
 * @author TestHub Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper即可，无需编写SQL
    // 提供的方法包括：
    // - selectById(id)
    // - selectOne(wrapper)
    // - selectList(wrapper)
    // - insert(entity)
    // - updateById(entity)
    // - deleteById(id)
}
