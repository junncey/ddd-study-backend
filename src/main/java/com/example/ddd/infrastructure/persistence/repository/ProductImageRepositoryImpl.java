package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.ProductImage;
import com.example.ddd.domain.repository.ProductImageRepository;
import com.example.ddd.infrastructure.persistence.mapper.ProductImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品图片仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class ProductImageRepositoryImpl implements ProductImageRepository {

    private final ProductImageMapper productImageMapper;

    @Override
    public ProductImage findById(Long id) {
        return productImageMapper.selectById(id);
    }

    @Override
    public ProductImage save(ProductImage entity) {
        if (entity.getId() == null) {
            productImageMapper.insert(entity);
        } else {
            productImageMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(ProductImage entity) {
        return productImageMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return productImageMapper.deleteById(id);
    }

    @Override
    public IPage<ProductImage> page(Page<ProductImage> page) {
        return productImageMapper.selectPage(page, null);
    }

    @Override
    public List<ProductImage> findByProductId(Long productId) {
        return productImageMapper.selectList(
                new LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, productId)
                        .orderByAsc(ProductImage::getSort)
        );
    }

    @Override
    public int deleteByProductId(Long productId) {
        return productImageMapper.delete(
                new LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, productId)
        );
    }
}
