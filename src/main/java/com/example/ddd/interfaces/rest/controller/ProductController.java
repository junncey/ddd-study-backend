package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.ProductApplicationService;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.model.entity.ProductImage;
import com.example.ddd.domain.model.entity.ProductSku;
import com.example.ddd.interfaces.rest.dto.ProductCreateRequest;
import com.example.ddd.interfaces.rest.dto.ProductUpdateRequest;
import com.example.ddd.interfaces.rest.vo.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 *
 * @author DDD Demo
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductApplicationService productApplicationService;

    /**
     * 获取商品列表
     */
    @GetMapping
    public Response<List<Product>> list(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        List<Product> products;
        if (shopId != null) {
            products = productApplicationService.getProductsByShopId(shopId);
        } else if (categoryId != null) {
            products = productApplicationService.getProductsByCategoryId(categoryId);
        } else {
            // 获取所有商品（分页查询第一页）
            products = productApplicationService.pageProducts(1L, 1000L, null, null, status).getRecords();
        }
        return Response.success(products);
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Response<Product> create(@RequestBody ProductCreateRequest request) {
        Product created = productApplicationService.createProductFromRequest(request);
        return Response.success(created);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Response<Product> update(@PathVariable Long id, @RequestBody ProductUpdateRequest request) {
        Product updated = productApplicationService.updateProductFromRequest(id, request);
        return Response.success(updated);
    }

    /**
     * 上架商品
     */
    @PutMapping("/{id}/on-sale")
    public Response<Product> onSale(@PathVariable Long id) {
        Product product = productApplicationService.onSale(id);
        return Response.success(product);
    }

    /**
     * 下架商品
     */
    @PutMapping("/{id}/off-sale")
    public Response<Product> offSale(@PathVariable Long id) {
        Product product = productApplicationService.offSale(id);
        return Response.success(product);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        productApplicationService.deleteProduct(id);
        return Response.success();
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Response<Product> getById(@PathVariable Long id) {
        Product product = productApplicationService.getProductById(id);
        return Response.success(product);
    }

    /**
     * 获取商品SKU列表
     */
    @GetMapping("/{id}/skus")
    public Response<List<ProductSku>> getSkus(@PathVariable Long id) {
        List<ProductSku> skus = productApplicationService.getProductSkus(id);
        return Response.success(skus);
    }

    /**
     * 获取商品图片列表
     */
    @GetMapping("/{id}/images")
    public Response<List<ProductImage>> getImages(@PathVariable Long id) {
        List<ProductImage> images = productApplicationService.getProductImages(id);
        return Response.success(images);
    }

    /**
     * 根据店铺获取商品列表
     */
    @GetMapping("/shop/{shopId}")
    public Response<List<Product>> getByShopId(@PathVariable Long shopId) {
        List<Product> products = productApplicationService.getProductsByShopId(shopId);
        return Response.success(products);
    }

    /**
     * 根据分类获取商品列表
     */
    @GetMapping("/category/{categoryId}")
    public Response<List<Product>> getByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productApplicationService.getProductsByCategoryId(categoryId);
        return Response.success(products);
    }

    /**
     * 分页查询商品
     */
    @GetMapping("/page")
    public Response<IPage<Product>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        IPage<Product> page = productApplicationService.pageProducts(current, size, shopId, categoryId, status);
        return Response.success(page);
    }
}
