package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.ReviewReply;
import com.example.ddd.domain.repository.ReviewReplyRepository;
import com.example.ddd.infrastructure.persistence.mapper.ReviewReplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评价回复仓储实现
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class ReviewReplyRepositoryImpl implements ReviewReplyRepository {

    private final ReviewReplyMapper reviewReplyMapper;

    @Override
    public ReviewReply findById(Long id) {
        return reviewReplyMapper.selectById(id);
    }

    @Override
    public ReviewReply save(ReviewReply entity) {
        if (entity.getId() == null) {
            reviewReplyMapper.insert(entity);
        } else {
            reviewReplyMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(ReviewReply entity) {
        return reviewReplyMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return reviewReplyMapper.deleteById(id);
    }

    @Override
    public IPage<ReviewReply> page(Page<ReviewReply> page) {
        return reviewReplyMapper.selectPage(page, null);
    }

    @Override
    public List<ReviewReply> findByReviewId(Long reviewId) {
        return reviewReplyMapper.selectList(
                new LambdaQueryWrapper<ReviewReply>()
                        .eq(ReviewReply::getReviewId, reviewId)
                        .orderByAsc(ReviewReply::getCreateTime)
        );
    }
}
