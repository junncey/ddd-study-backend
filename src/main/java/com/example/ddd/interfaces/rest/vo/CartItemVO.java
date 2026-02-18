package com.example.ddd.interfaces.rest.vo;

import com.example.ddd.domain.model.entity.CartItem;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.model.entity.ProductSku;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车明细VO
 * 包含购物车项和关联的商品信息
 *
 * @author DDD Demo
 */
@Data
public class CartItemVO {

    /**
     * 购物车明细ID
     */
    private Long id;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 价格快照
     */
    private BigDecimal priceSnapshot;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;

    /**
     * 从实体创建CartItemVO
     *
     * @param cartItem 购物车明细
     * @param sku      商品SKU
     * @param product  商品
     * @return CartItemVO
     */
    public static CartItemVO from(CartItem cartItem, ProductSku sku, Product product) {
        CartItemVO vo = new CartItemVO();
        vo.setId(cartItem.getId());
        vo.setSkuId(cartItem.getSkuId());
        vo.setQuantity(cartItem.getQuantity());

        // 价格快照
        if (cartItem.getPriceSnapshot() != null) {
            vo.setPriceSnapshot(cartItem.getPriceSnapshot().getValue());
            vo.setSubtotal(cartItem.getSubtotal().getValue());
        }

        // SKU信息
        if (sku != null) {
            vo.setSkuName(sku.getSkuName());
            vo.setProductId(sku.getProductId());
        }

        // 商品信息
        if (product != null) {
            vo.setProductName(product.getProductName());
            vo.setMainImage(product.getMainImage());
        }

        return vo;
    }
}
