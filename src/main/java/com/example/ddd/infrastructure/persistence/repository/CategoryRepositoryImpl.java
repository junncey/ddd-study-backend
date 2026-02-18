package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Category;
import com.example.ddd.domain.repository.CategoryRepository;
import com.example.ddd.infrastructure.persistence.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    @Override
    public Category findById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public Category save(Category entity) {
        if (entity.getId() == null) {
            categoryMapper.insert(entity);
        } else {
            categoryMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Category entity) {
        return categoryMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return categoryMapper.deleteById(id);
    }

    @Override
    public IPage<Category> page(Page<Category> page) {
        return categoryMapper.selectPage(page, null);
    }

    @Override
    public List<Category> findTopLevelCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, 0L)
                        .orderByAsc(Category::getSort)
        );
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, parentId)
                        .orderByAsc(Category::getSort)
        );
    }

    @Override
    public List<Category> findByLevel(Integer level) {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getLevel, level)
                        .orderByAsc(Category::getSort)
        );
    }

    @Override
    public List<Category> buildCategoryTree() {
        // 查询所有分类
        List<Category> allCategories = categoryMapper.selectList(null);

        // 构建分类树
        List<Category> topLevelCategories = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.getParentId() != null && category.getParentId().equals(0L)) {
                topLevelCategories.add(category);
            }
        }

        // 递归构建子分类树
        for (Category topCategory : topLevelCategories) {
            buildChildren(topCategory, allCategories);
        }

        return topLevelCategories;
    }

    /**
     * 递归构建子分类树
     *
     * @param parent   父分类
     * @param allCategories 所有分类列表
     */
    private void buildChildren(Category parent, List<Category> allCategories) {
        List<Category> children = allCategories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId().equals(parent.getId()))
                .collect(Collectors.toList());

        for (Category child : children) {
            buildChildren(child, allCategories);
        }
    }
}
