package com.example.ddd.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.model.valueobject.ProductStatus;

import java.util.List;

/**
 * 商品仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface ProductRepository extends BaseRepository<Product> {

    /**
     * 根据店铺ID查询商品
     *
     * @param shopId 店铺ID
     * @return 商品列表
     */
    List<Product> findByShopId(Long shopId);

    /**
     * 根据分类ID查询商品
     *
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * 根据状态查询商品
     *
     * @param status 状态
     * @return 商品列表
     */
    List<Product> findByStatus(ProductStatus status);

    /**
     * 分页查询店铺的商品
     *
     * @param page   分页对象
     * @param shopId 店铺ID
     * @return 分页结果
     */
    IPage<Product> pageByShopId(Page<Product> page, Long shopId);

    /**
     * 分页查询在售商品
     *
     * @param page 分页对象
     * @return 分页结果
     */
    IPage<Product> pageOnSale(Page<Product> page);
}
