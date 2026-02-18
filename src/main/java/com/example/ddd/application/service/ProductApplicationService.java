package com.example.ddd.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.model.entity.ProductImage;
import com.example.ddd.domain.model.entity.ProductSku;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.domain.model.valueobject.ProductStatus;
import com.example.ddd.domain.model.valueobject.Quantity;
import com.example.ddd.domain.repository.ProductImageRepository;
import com.example.ddd.domain.repository.ProductRepository;
import com.example.ddd.domain.repository.ProductSkuRepository;
import com.example.ddd.domain.service.ProductDomainService;
import com.example.ddd.interfaces.rest.dto.ProductCreateRequest;
import com.example.ddd.interfaces.rest.dto.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 商品应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductApplicationService extends ApplicationService {

    private final ProductDomainService productDomainService;
    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductImageRepository productImageRepository;

    /**
     * 创建商品
     */
    public Product createProduct(Product product, List<ProductSku> skuList, List<ProductImage> imageList) {
        beforeExecute();
        try {
            return productDomainService.createProduct(product, skuList, imageList);
        } finally {
            afterExecute();
        }
    }

    /**
     * 更新商品
     */
    public Product updateProduct(Product product, List<ProductSku> skuList, List<ProductImage> imageList) {
        beforeExecute();
        try {
            return productDomainService.updateProduct(product, skuList, imageList);
        } finally {
            afterExecute();
        }
    }

    /**
     * 上架商品
     */
    public Product onSale(Long productId) {
        beforeExecute();
        try {
            return productDomainService.onSale(productId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 下架商品
     */
    public Product offSale(Long productId) {
        beforeExecute();
        try {
            return productDomainService.offSale(productId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 删除商品
     */
    public boolean deleteProduct(Long productId) {
        beforeExecute();
        try {
            return productDomainService.deleteProduct(productId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取商品详情
     */
    public Product getProductById(Long productId) {
        beforeExecute();
        try {
            return productRepository.findById(productId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取商品SKU列表
     */
    public List<ProductSku> getProductSkus(Long productId) {
        beforeExecute();
        try {
            return productSkuRepository.findByProductId(productId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取商品图片列表
     */
    public List<ProductImage> getProductImages(Long productId) {
        beforeExecute();
        try {
            return productImageRepository.findByProductId(productId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 根据店铺获取商品列表
     */
    public List<Product> getProductsByShopId(Long shopId) {
        beforeExecute();
        try {
            return productRepository.findByShopId(shopId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 根据分类获取商品列表
     */
    public List<Product> getProductsByCategoryId(Long categoryId) {
        beforeExecute();
        try {
            return productRepository.findByCategoryId(categoryId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 分页查询商品
     */
    public IPage<Product> pageProducts(Long current, Long size, Long shopId, Long categoryId, Integer status) {
        beforeExecute();
        try {
            if (shopId != null) {
                return productRepository.pageByShopId(new Page<>(current, size), shopId);
            }
            return productRepository.pageOnSale(new Page<>(current, size));
        } finally {
            afterExecute();
        }
    }

    /**
     * 从请求DTO创建商品（同时创建默认SKU）
     */
    public Product createProductFromRequest(ProductCreateRequest request) {
        beforeExecute();
        try {
            // 创建商品
            Product product = new Product();
            product.setShopId(request.getShopId());
            product.setCategoryId(request.getCategoryId());
            product.setProductName(request.getProductName());
            product.setProductDesc(request.getProductDesc());
            product.setMainImage(request.getMainImage());
            if (request.getStatus() != null) {
                product.setStatus(ProductStatus.fromValue(request.getStatus()));
            } else {
                product.setStatus(ProductStatus.DRAFT);
            }

            // 创建默认SKU
            ProductSku defaultSku = null;
            if (request.getPrice() != null && request.getStock() != null) {
                defaultSku = new ProductSku();
                defaultSku.setSkuName("默认规格");
                defaultSku.setPrice(Money.of(request.getPrice()));
                defaultSku.setStock(Quantity.of(request.getStock()));
                defaultSku.setSpecs("[]");
            }

            return productDomainService.createProduct(product,
                    defaultSku != null ? Collections.singletonList(defaultSku) : null,
                    null);
        } finally {
            afterExecute();
        }
    }

    /**
     * 从请求DTO更新商品（同时更新默认SKU）
     */
    public Product updateProductFromRequest(Long productId, ProductUpdateRequest request) {
        beforeExecute();
        try {
            // 获取现有商品
            Product product = productRepository.findById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            // 更新商品信息
            if (request.getCategoryId() != null) {
                product.setCategoryId(request.getCategoryId());
            }
            if (request.getProductName() != null) {
                product.setProductName(request.getProductName());
            }
            if (request.getProductDesc() != null) {
                product.setProductDesc(request.getProductDesc());
            }
            if (request.getMainImage() != null) {
                product.setMainImage(request.getMainImage());
            }
            if (request.getStatus() != null) {
                product.setStatus(ProductStatus.fromValue(request.getStatus()));
            }

            // 更新默认SKU（如果存在）
            ProductSku defaultSku = null;
            if (request.getPrice() != null || request.getStock() != null) {
                List<ProductSku> skus = productSkuRepository.findByProductId(productId);
                if (!skus.isEmpty()) {
                    defaultSku = skus.get(0);
                    if (request.getPrice() != null) {
                        defaultSku.setPrice(Money.of(request.getPrice()));
                    }
                    if (request.getStock() != null) {
                        defaultSku.setStock(Quantity.of(request.getStock()));
                    }
                } else if (request.getPrice() != null && request.getStock() != null) {
                    // 如果没有SKU，创建默认SKU
                    defaultSku = new ProductSku();
                    defaultSku.setProductId(productId);
                    defaultSku.setSkuName("默认规格");
                    defaultSku.setPrice(Money.of(request.getPrice()));
                    defaultSku.setStock(Quantity.of(request.getStock()));
                    defaultSku.setSpecs("[]");
                }
            }

            return productDomainService.updateProduct(product,
                    defaultSku != null ? Collections.singletonList(defaultSku) : null,
                    null);
        } finally {
            afterExecute();
        }
    }
}
