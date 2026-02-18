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
     * 根据多个分类ID查询商品
     *
     * @param categoryIds 分类ID列表
     * @return 商品列表
     */
    List<Product> findByCategoryIds(List<Long> categoryIds);

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

    /**
     * 搜索商品（按名称模糊查询）
     *
     * @param keyword 搜索关键词
     * @return 商品列表
     */
    List<Product> searchByKeyword(String keyword);

    /**
     * 分页搜索商品（按名称模糊查询）
     *
     * @param page    分页对象
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<Product> pageSearchByKeyword(Page<Product> page, String keyword);
}
