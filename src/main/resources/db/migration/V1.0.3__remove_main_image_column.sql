-- =====================================================
-- 删除商品表中的 main_image 列
-- V1.0.3__remove_main_image_column.sql
-- 图片统一使用 t_file 表管理
-- =====================================================

-- 删除 main_image 列
ALTER TABLE t_product DROP COLUMN IF EXISTS main_image;
