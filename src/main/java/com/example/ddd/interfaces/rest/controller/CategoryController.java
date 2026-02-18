package com.example.ddd.interfaces.rest.controller;

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

    /**
     * 创建分类
     */
    @PostMapping
    public Response<Category> create(@RequestBody Category category) {
        Category created = categoryApplicationService.createCategory(category);
        return Response.success(created);
    }

    /**
     * 更新分类
     */
    @PutMapping
    public Response<Category> update(@RequestBody Category category) {
        Category updated = categoryApplicationService.updateCategory(category);
        return Response.success(updated);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        categoryApplicationService.deleteCategory(id);
        return Response.success();
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Response<Category> getById(@PathVariable Long id) {
        Category category = categoryApplicationService.getCategoryById(id);
        return Response.success(category);
    }

    /**
     * 获取子分类
     */
    @GetMapping("/{id}/children")
    public Response<List<Category>> getChildren(@PathVariable Long id) {
        List<Category> children = categoryApplicationService.getChildren(id);
        return Response.success(children);
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Response<List<Category>> getTree() {
        List<Category> tree = categoryApplicationService.getCategoryTree();
        return Response.success(tree);
    }

    /**
     * 获取所有分类
     */
    @GetMapping
    public Response<List<Category>> getAll() {
        List<Category> categories = categoryApplicationService.getAllCategories();
        return Response.success(categories);
    }
}
