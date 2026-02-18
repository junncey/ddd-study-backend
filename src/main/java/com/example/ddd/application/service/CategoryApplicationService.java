package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Category;
import com.example.ddd.domain.repository.CategoryRepository;
import com.example.ddd.domain.service.CategoryDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryApplicationService extends ApplicationService {

    private final CategoryDomainService categoryDomainService;
    private final CategoryRepository categoryRepository;

    /**
     * 创建分类
     */
    public Category createCategory(Category category) {
        beforeExecute();
        try {
            return categoryDomainService.createCategory(category);
        } finally {
            afterExecute();
        }
    }

    /**
     * 更新分类
     */
    public Category updateCategory(Category category) {
        beforeExecute();
        try {
            return categoryDomainService.updateCategory(category);
        } finally {
            afterExecute();
        }
    }

    /**
     * 删除分类
     */
    public boolean deleteCategory(Long categoryId) {
        beforeExecute();
        try {
            return categoryDomainService.deleteCategory(categoryId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取分类详情
     */
    public Category getCategoryById(Long categoryId) {
        beforeExecute();
        try {
            return categoryRepository.findById(categoryId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取子分类
     */
    public List<Category> getChildren(Long parentId) {
        beforeExecute();
        try {
            return categoryRepository.findByParentId(parentId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取分类树
     */
    public List<Category> getCategoryTree() {
        beforeExecute();
        try {
            return categoryDomainService.getCategoryTree();
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取所有分类
     */
    public List<Category> getAllCategories() {
        beforeExecute();
        try {
            return categoryRepository.findTopLevelCategories();
        } finally {
            afterExecute();
        }
    }
}
