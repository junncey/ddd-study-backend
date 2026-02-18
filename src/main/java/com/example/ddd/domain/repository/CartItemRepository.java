package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.CartItem;

import java.util.List;

/**
 * 购物车明细仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface CartItemRepository extends BaseRepository<CartItem> {

    /**
     * 根据购物车ID查询明细列表
     *
     * @param cartId 购物车ID
     * @return 明细列表
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * 根据购物车ID和SKU ID查询明细
     *
     * @param cartId 购物车ID
     * @param skuId   SKU ID
     * @return 明细对象
     */
    CartItem findByCartIdAndSkuId(Long cartId, Long skuId);

    /**
     * 删除购物车的所有明细
     *
     * @param cartId 购物车ID
     * @return 影响行数
     */
    int deleteByCartId(Long cartId);

    /**
     * 物理删除指定购物车和SKU的已删除记录
     * 用于解决唯一索引冲突问题
     *
     * @param cartId 购物车ID
     * @param skuId  SKU ID
     * @return 影响行数
     */
    int physicalDeleteByCartIdAndSkuId(Long cartId, Long skuId);

    /**
     * 物理删除指定ID的购物车明细
     *
     * @param id 购物车明细ID
     * @return 影响行数
     */
    int physicalDeleteById(Long id);

    /**
     * 物理删除指定购物车的所有明细
     * 用于清空购物车时避免逻辑删除的唯一约束冲突
     *
     * @param cartId 购物车ID
     * @return 影响行数
     */
    int physicalDeleteByCartId(Long cartId);
}
