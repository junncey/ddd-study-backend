package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.ProductSku;
import com.example.ddd.domain.repository.ProductSkuRepository;
import com.example.ddd.infrastructure.persistence.mapper.ProductSkuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品SKU仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class ProductSkuRepositoryImpl implements ProductSkuRepository {

    private final ProductSkuMapper productSkuMapper;

    @Override
    public ProductSku findById(Long id) {
        return productSkuMapper.selectById(id);
    }

    @Override
    public ProductSku save(ProductSku entity) {
        if (entity.getId() == null) {
            productSkuMapper.insert(entity);
        } else {
            productSkuMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(ProductSku entity) {
        return productSkuMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return productSkuMapper.deleteById(id);
    }

    @Override
    public IPage<ProductSku> page(Page<ProductSku> page) {
        return productSkuMapper.selectPage(page, null);
    }

    @Override
    public List<ProductSku> findByProductId(Long productId) {
        return productSkuMapper.selectList(
                new LambdaQueryWrapper<ProductSku>()
                        .eq(ProductSku::getProductId, productId)
        );
    }

    @Override
    public int decreaseStock(Long skuId, Integer quantity, Integer currentStock) {
        LambdaUpdateWrapper<ProductSku> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ProductSku::getId, skuId)
                .eq(ProductSku::getStock, currentStock)  // 乐观锁
                .ge(ProductSku::getStock, quantity)
                .setSql("stock = stock - " + quantity);
        return productSkuMapper.update(null, wrapper);
    }

    @Override
    public int increaseStock(Long skuId, Integer quantity) {
        LambdaUpdateWrapper<ProductSku> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ProductSku::getId, skuId)
                .setSql("stock = stock + " + quantity);
        return productSkuMapper.update(null, wrapper);
    }
}
