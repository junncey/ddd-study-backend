package com.example.ddd.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 仓储接口基类
 * 领域层与基础设施层之间的契约（六边形架构的端口）
 *
 * @param <T> 实体类型
 * @author DDD Demo
 */
public interface BaseRepository<T> {

    /**
     * 根据ID查找实体
     *
     * @param id ID
     * @return 实体对象
     */
    T findById(Long id);

    /**
     * 保存实体
     *
     * @param entity 实体对象
     * @return 保存后的实体
     */
    T save(T entity);

    /**
     * 更新实体
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(T entity);

    /**
     * 删除实体
     *
     * @param id ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @return 分页结果
     */
    IPage<T> page(Page<T> page);
}
