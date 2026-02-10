package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 * 六边形架构的适配器，实现领域层定义的 UserRepository 接口
 *
 * @author DDD Demo
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
