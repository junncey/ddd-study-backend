package com.example.ddd.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Shop;

import java.util.List;

/**
 * 店铺仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface ShopRepository extends BaseRepository<Shop> {

    /**
     * 根据店主ID查询店铺
     *
     * @param ownerId 店主ID
     * @return 店铺对象
     */
    Shop findByOwnerId(Long ownerId);

    /**
     * 检查店主是否已有店铺
     *
     * @param ownerId 店主ID
     * @return true 如果已存在店铺
     */
    boolean existsByOwnerId(Long ownerId);

    /**
     * 根据状态查询店铺列表
     *
     * @param status 状态
     * @return 店铺列表
     */
    List<Shop> findByStatus(com.example.ddd.domain.model.valueobject.ShopStatus status);

    /**
     * 分页查询店铺
     *
     * @param page   分页对象
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Shop> page(Page<Shop> page, com.example.ddd.domain.model.valueobject.ShopStatus status);
}
