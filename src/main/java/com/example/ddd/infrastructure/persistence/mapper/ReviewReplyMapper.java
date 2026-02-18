package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.ReviewReply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评价回复 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface ReviewReplyMapper extends BaseMapper<ReviewReply> {
}
