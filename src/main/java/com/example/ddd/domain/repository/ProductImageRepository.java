package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.ProductImage;

import java.util.List;

/**
 * 商品图片仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface ProductImageRepository extends BaseRepository<ProductImage> {

    /**
     * 根据商品ID查询图片列表
     *
     * @param productId 商品ID
     * @return 图片列表（按排序序号）
     */
    List<ProductImage> findByProductId(Long productId);

    /**
     * 删除商品的所有图片
     *
     * @param productId 商品ID
     * @return 影响行数
     */
    int deleteByProductId(Long productId);
}
