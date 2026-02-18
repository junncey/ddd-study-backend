package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Shop;
import com.example.ddd.domain.model.valueobject.ShopStatus;
import com.example.ddd.domain.repository.ShopRepository;
import com.example.ddd.infrastructure.persistence.mapper.ShopMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 店铺仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class ShopRepositoryImpl implements ShopRepository {

    private final ShopMapper shopMapper;

    @Override
    public Shop findById(Long id) {
        return shopMapper.selectById(id);
    }

    @Override
    public Shop save(Shop entity) {
        if (entity.getId() == null) {
            shopMapper.insert(entity);
        } else {
            shopMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Shop entity) {
        return shopMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return shopMapper.deleteById(id);
    }

    @Override
    public IPage<Shop> page(Page<Shop> page) {
        return shopMapper.selectPage(page, null);
    }

    @Override
    public Shop findByOwnerId(Long ownerId) {
        return shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>()
                        .eq(Shop::getOwnerId, ownerId)
        );
    }

    @Override
    public boolean existsByOwnerId(Long ownerId) {
        return shopMapper.selectCount(
                new LambdaQueryWrapper<Shop>()
                        .eq(Shop::getOwnerId, ownerId)
        ) > 0;
    }

    @Override
    public List<Shop> findByStatus(ShopStatus status) {
        return shopMapper.selectList(
                new LambdaQueryWrapper<Shop>()
                        .eq(Shop::getStatus, status)
        );
    }

    @Override
    public IPage<Shop> page(Page<Shop> page, ShopStatus status) {
        if (status == null) {
            return shopMapper.selectPage(page, null);
        }
        return shopMapper.selectPage(page,
                new LambdaQueryWrapper<Shop>()
                        .eq(Shop::getStatus, status)
        );
    }
}
