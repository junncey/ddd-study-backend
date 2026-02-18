package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类实体（树形结构）
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_category")
public class Category extends BaseEntity {

    /**
     * 父分类ID 0表示顶级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类图标
     */
    private String categoryIcon;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 分类路径 如: 1/2/3
     */
    private String path;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 判断是否为顶级分类
     *
     * @return true 如果为顶级分类
     */
    public boolean isTopLevel() {
        return parentId != null && parentId.equals(0L);
    }

    /**
     * 构建分类路径
     *
     * @param parentPath 父级路径
     * @return 当前分类的路径
     */
    public String buildPath(String parentPath) {
        if (isTopLevel()) {
            return String.valueOf(getId());
        }
        return parentPath + "/" + getId();
    }
}
