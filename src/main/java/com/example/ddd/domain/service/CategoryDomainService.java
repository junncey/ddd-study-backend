package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Category;
import com.example.ddd.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类领域服务
 * 包含分类相关的核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryDomainService extends DomainService {

    private final CategoryRepository categoryRepository;

    /**
     * 创建分类
     * 计算分类层级和路径
     *
     * @param category 分类实体
     * @return 创建后的分类
     */
    @Transactional(rollbackFor = Exception.class)
    public Category createCategory(Category category) {
        validate();

        // 设置层级和路径
        if (category.getParentId() != null && category.getParentId() > 0) {
            Category parent = categoryRepository.findById(category.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("父分类不存在");
            }
            category.setLevel(parent.getLevel() + 1);
            category.setPath(category.buildPath(parent.getPath()));
        } else {
            category.setParentId(0L);
            category.setLevel(1);
            category.setPath(null); // 保存后会更新
        }

        Category saved = categoryRepository.save(category);

        // 更新路径
        if (saved.getPath() == null) {
            saved.setPath(String.valueOf(saved.getId()));
            categoryRepository.update(saved);
        }

        return saved;
    }

    /**
     * 更新分类
     *
     * @param category 分类实体
     * @return 更新后的分类
     */
    @Transactional(rollbackFor = Exception.class)
    public Category updateCategory(Category category) {
        validate();

        // 验证分类是否存在
        Category existing = categoryRepository.findById(category.getId());
        if (existing == null) {
            throw new IllegalArgumentException("分类不存在");
        }

        // 如果修改了父分类，需要重新计算层级和路径
        if (!existing.getParentId().equals(category.getParentId())) {
            if (category.getParentId() > 0) {
                Category parent = categoryRepository.findById(category.getParentId());
                if (parent == null) {
                    throw new IllegalArgumentException("父分类不存在");
                }
                category.setLevel(parent.getLevel() + 1);
                category.setPath(category.buildPath(parent.getPath()));
            } else {
                category.setLevel(1);
                category.setPath(String.valueOf(category.getId()));
            }
        }

        return categoryRepository.save(category);
    }

    /**
     * 删除分类
     * 检查是否有子分类，如果有则不允许删除
     *
     * @param categoryId 分类ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long categoryId) {
        validate();

        // 检查是否有子分类
        List<Category> children = categoryRepository.findByParentId(categoryId);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("该分类下有子分类，不能删除");
        }

        return categoryRepository.delete(categoryId) > 0;
    }

    /**
     * 获取分类树
     *
     * @return 分类树列表
     */
    public List<Category> getCategoryTree() {
        validate();
        return categoryRepository.buildCategoryTree();
    }
}
