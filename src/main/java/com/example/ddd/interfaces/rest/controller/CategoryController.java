package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.AuthorizationService;
import com.example.ddd.application.service.CategoryApplicationService;
import com.example.ddd.domain.model.entity.Category;
import com.example.ddd.interfaces.rest.vo.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 *
 * @author DDD Demo
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryApplicationService categoryApplicationService;
    private final AuthorizationService authorizationService;

    /**
     * 创建分类（管理员操作）
     */
    @PostMapping
    public Response<Category> create(@RequestBody Category category) {
        // 验证管理员权限
        authorizationService.checkAdminPermission();
        Category created = categoryApplicationService.createCategory(category);
        return Response.success(created);
    }

    /**
     * 更新分类（管理员操作）
     */
    @PutMapping
    public Response<Category> update(@RequestBody Category category) {
        // 验证管理员权限
        authorizationService.checkAdminPermission();
        Category updated = categoryApplicationService.updateCategory(category);
        return Response.success(updated);
    }

    /**
     * 删除分类（管理员操作）
     */
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        // 验证管理员权限
        authorizationService.checkAdminPermission();
        categoryApplicationService.deleteCategory(id);
        return Response.success();
    }

    /**
     * 获取分类详情（公开）
     */
    @GetMapping("/{id}")
    public Response<Category> getById(@PathVariable Long id) {
        Category category = categoryApplicationService.getCategoryById(id);
        return Response.success(category);
    }

    /**
     * 获取子分类（公开）
     */
    @GetMapping("/{id}/children")
    public Response<List<Category>> getChildren(@PathVariable Long id) {
        List<Category> children = categoryApplicationService.getChildren(id);
        return Response.success(children);
    }

    /**
     * 获取分类树（公开）
     */
    @GetMapping("/tree")
    public Response<List<Category>> getTree() {
        List<Category> tree = categoryApplicationService.getCategoryTree();
        return Response.success(tree);
    }

    /**
     * 获取所有分类（公开）
     */
    @GetMapping
    public Response<List<Category>> getAll() {
        List<Category> categories = categoryApplicationService.getAllCategories();
        return Response.success(categories);
    }
}
