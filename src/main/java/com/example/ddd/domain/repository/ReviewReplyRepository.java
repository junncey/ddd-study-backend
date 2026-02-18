package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.ReviewReply;

import java.util.List;

/**
 * 评价回复仓储接口
 *
 * @author DDD Demo
 */
public interface ReviewReplyRepository extends BaseRepository<ReviewReply> {

    /**
     * 根据评价ID查询回复列表
     */
    List<ReviewReply> findByReviewId(Long reviewId);
}
