package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Favorite;
import com.example.ddd.domain.repository.FavoriteRepository;
import com.example.ddd.infrastructure.persistence.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收藏仓储实现
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepository {

    private final FavoriteMapper favoriteMapper;

    @Override
    public Favorite findById(Long id) {
        return favoriteMapper.selectById(id);
    }

    @Override
    public Favorite save(Favorite entity) {
        if (entity.getId() == null) {
            favoriteMapper.insert(entity);
        } else {
            favoriteMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Favorite entity) {
        return favoriteMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return favoriteMapper.deleteById(id);
    }

    @Override
    public IPage<Favorite> page(Page<Favorite> page) {
        return favoriteMapper.selectPage(page, null);
    }

    @Override
    public Favorite findByUserIdAndProductId(Long userId, Long productId) {
        return favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getProductId, productId)
        );
    }

    @Override
    public List<Favorite> findByUserId(Long userId) {
        return favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime)
        );
    }

    @Override
    public List<Favorite> findByShopId(Long shopId) {
        return favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getShopId, shopId)
                        .orderByDesc(Favorite::getCreateTime)
        );
    }

    @Override
    public long countByUserId(Long userId) {
        return favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
        );
    }

    @Override
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getProductId, productId)
        ) > 0;
    }

    @Override
    public int deleteByUserIdAndProductId(Long userId, Long productId) {
        return favoriteMapper.delete(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getProductId, productId)
        );
    }
}
