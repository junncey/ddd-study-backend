package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
