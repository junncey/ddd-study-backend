package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Review;
import com.example.ddd.domain.repository.ReviewRepository;
import com.example.ddd.infrastructure.persistence.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评价仓储实现
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewMapper reviewMapper;

    @Override
    public Review findById(Long id) {
        return reviewMapper.selectById(id);
    }

    @Override
    public Review save(Review entity) {
        if (entity.getId() == null) {
            reviewMapper.insert(entity);
        } else {
            reviewMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Review entity) {
        return reviewMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return reviewMapper.deleteById(id);
    }

    @Override
    public IPage<Review> page(Page<Review> page) {
        return reviewMapper.selectPage(page, null);
    }

    @Override
    public Review findByOrderItemId(Long orderItemId) {
        return reviewMapper.selectOne(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getOrderItemId, orderItemId)
        );
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getProductId, productId)
                        .orderByDesc(Review::getCreateTime)
        );
    }

    @Override
    public List<Review> findByShopId(Long shopId) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getShopId, shopId)
                        .orderByDesc(Review::getCreateTime)
        );
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getUserId, userId)
                        .orderByDesc(Review::getCreateTime)
        );
    }

    @Override
    public long countByProductId(Long productId) {
        return reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getProductId, productId)
        );
    }

    @Override
    public long countPositiveByProductId(Long productId) {
        return reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getProductId, productId)
                        .ge(Review::getRating, 4)
        );
    }
}
