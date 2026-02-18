package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.model.valueobject.ProductStatus;
import com.example.ddd.domain.repository.ProductRepository;
import com.example.ddd.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;

    @Override
    public Product findById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public Product save(Product entity) {
        if (entity.getId() == null) {
            productMapper.insert(entity);
        } else {
            productMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Product entity) {
        return productMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return productMapper.deleteById(id);
    }

    @Override
    public IPage<Product> page(Page<Product> page) {
        return productMapper.selectPage(page, null);
    }

    @Override
    public List<Product> findByShopId(Long shopId) {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getShopId, shopId)
                        .orderByDesc(Product::getCreateTime)
        );
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getCategoryId, categoryId)
                        .orderByDesc(Product::getCreateTime)
        );
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, status)
                        .orderByDesc(Product::getCreateTime)
        );
    }

    @Override
    public IPage<Product> pageByShopId(Page<Product> page, Long shopId) {
        return productMapper.selectPage(page,
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getShopId, shopId)
                        .orderByDesc(Product::getCreateTime)
        );
    }

    @Override
    public IPage<Product> pageOnSale(Page<Product> page) {
        return productMapper.selectPage(page,
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, ProductStatus.ON_SALE)
                        .orderByDesc(Product::getCreateTime)
        );
    }
}
