package com.example.ddd.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.ProductSku;

import java.util.List;

/**
 * 商品SKU仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface ProductSkuRepository extends BaseRepository<ProductSku> {

    /**
     * 根据商品ID查询SKU列表
     *
     * @param productId 商品ID
     * @return SKU列表
     */
    List<ProductSku> findByProductId(Long productId);

    /**
     * 扣减库存（乐观锁）
     *
     * @param skuId        SKU ID
     * @param quantity      扣减数量
     * @param currentStock 当前库存（乐观锁版本）
     * @return 影响行数，0表示库存不足或版本冲突
     */
    int decreaseStock(Long skuId, Integer quantity, Integer currentStock);

    /**
     * 增加库存
     *
     * @param skuId   SKU ID
     * @param quantity 增加数量
     * @return 影响行数
     */
    int increaseStock(Long skuId, Integer quantity);
}
