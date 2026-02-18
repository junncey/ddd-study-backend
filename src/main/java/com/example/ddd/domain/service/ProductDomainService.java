package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.model.entity.ProductImage;
import com.example.ddd.domain.model.entity.ProductSku;
import com.example.ddd.domain.model.valueobject.ProductStatus;
import com.example.ddd.domain.repository.ProductImageRepository;
import com.example.ddd.domain.repository.ProductRepository;
import com.example.ddd.domain.repository.ProductSkuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品领域服务
 * 包含商品相关的核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDomainService extends DomainService {

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductImageRepository productImageRepository;

    /**
     * 创建商品
     *
     * @param product       商品实体
     * @param skuList       SKU列表
     * @param imageList     图片列表
     * @return 创建后的商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Product createProduct(Product product, List<ProductSku> skuList, List<ProductImage> imageList) {
        validate();

        // 设置初始状态为草稿
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.DRAFT);
        }

        // 保存商品
        Product saved = productRepository.save(product);

        // 保存SKU
        if (skuList != null && !skuList.isEmpty()) {
            for (ProductSku sku : skuList) {
                sku.setProductId(saved.getId());
                productSkuRepository.save(sku);
            }
        }

        // 保存图片
        if (imageList != null && !imageList.isEmpty()) {
            for (ProductImage image : imageList) {
                image.setProductId(saved.getId());
                productImageRepository.save(image);
            }
        }

        return saved;
    }

    /**
     * 更新商品
     *
     * @param product       商品实体
     * @param skuList       SKU列表
     * @param imageList     图片列表
     * @return 更新后的商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Product updateProduct(Product product, List<ProductSku> skuList, List<ProductImage> imageList) {
        validate();

        // 验证商品是否存在
        Product existing = productRepository.findById(product.getId());
        if (existing == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        // 更新商品基本信息
        productRepository.update(product);

        // 更新SKU
        if (skuList != null) {
            for (ProductSku sku : skuList) {
                sku.setProductId(product.getId());
                productSkuRepository.save(sku);
            }
        }

        // 更新图片
        if (imageList != null) {
            // 删除旧图片
            productImageRepository.deleteByProductId(product.getId());
            // 添加新图片
            for (ProductImage image : imageList) {
                image.setProductId(product.getId());
                productImageRepository.save(image);
            }
        }

        return product;
    }

    /**
     * 上架商品
     *
     * @param productId 商品ID
     * @return 更新后的商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Product onSale(Long productId) {
        validate();

        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        if (!product.getStatus().canOnSale()) {
            throw new IllegalArgumentException("当前状态不允许上架");
        }

        product.setStatus(ProductStatus.ON_SALE);
        return productRepository.save(product);
    }

    /**
     * 下架商品
     *
     * @param productId 商品ID
     * @return 更新后的商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Product offSale(Long productId) {
        validate();

        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        if (!product.getStatus().canOffline()) {
            throw new IllegalArgumentException("当前状态不允许下架");
        }

        product.setStatus(ProductStatus.OFFLINE);
        return productRepository.save(product);
    }

    /**
     * 删除商品
     * 检查商品是否有关联的订单，如果有则不允许删除
     *
     * @param productId 商品ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long productId) {
        validate();

        // TODO: 检查是否有关联的订单

        // 删除商品
        productRepository.delete(productId);

        // 删除SKU
        List<ProductSku> skuList = productSkuRepository.findByProductId(productId);
        for (ProductSku sku : skuList) {
            productSkuRepository.delete(sku.getId());
        }

        // 删除图片
        productImageRepository.deleteByProductId(productId);

        return true;
    }

    /**
     * 验证库存是否充足
     *
     * @param skuId         SKU ID
     * @param requiredQuantity 需要的数量
     * @return true 如果库存充足
     */
    public boolean validateStock(Long skuId, Integer requiredQuantity) {
        validate();

        ProductSku sku = productSkuRepository.findById(skuId);
        if (sku == null) {
            throw new IllegalArgumentException("SKU不存在");
        }

        return sku.hasEnoughStock(requiredQuantity);
    }
}
