-- =====================================================
-- 修复购物车明细表唯一索引问题
-- 版本: V1.0.1
-- 描述: 修改唯一索引，包含 deleted 字段，解决逻辑删除后再次添加商品报错的问题
-- =====================================================

-- 删除旧的唯一索引（不包含 deleted 字段）
ALTER TABLE `t_cart_item` DROP INDEX `uk_cart_sku`;

-- 创建新的唯一索引（包含 deleted 字段）
-- 这样逻辑删除的记录（deleted=1）不会阻止新建记录
ALTER TABLE `t_cart_item` ADD UNIQUE INDEX `uk_cart_sku_deleted` (`cart_id`, `sku_id`, `deleted`);
