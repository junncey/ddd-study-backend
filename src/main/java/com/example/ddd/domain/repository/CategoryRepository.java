package com.example.ddd.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Category;

import java.util.List;

/**
 * 分类仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface CategoryRepository extends BaseRepository<Category> {

    /**
     * 查询所有顶级分类
     *
     * @return 顶级分类列表
     */
    List<Category> findTopLevelCategories();

    /**
     * 根据父分类ID查询子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 根据层级查询分类
     *
     * @param level 层级
     * @return 分类列表
     */
    List<Category> findByLevel(Integer level);

    /**
     * 构建分类树
     *
     * @return 分类树列表
     */
    List<Category> buildCategoryTree();
}
