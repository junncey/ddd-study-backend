package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 购物车明细 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    /**
     * 物理删除指定购物车和SKU的已删除记录
     *
     * @param cartId 购物车ID
     * @param skuId  SKU ID
     * @return 影响行数
     */
    @Delete("DELETE FROM t_cart_item WHERE cart_id = #{cartId} AND sku_id = #{skuId} AND deleted = 1")
    int physicalDeleteByCartIdAndSkuId(@Param("cartId") Long cartId, @Param("skuId") Long skuId);

    /**
     * 物理删除指定ID的购物车明细
     *
     * @param id 购物车明细ID
     * @return 影响行数
     */
    @Delete("DELETE FROM t_cart_item WHERE id = #{id}")
    int physicalDeleteById(@Param("id") Long id);
}
