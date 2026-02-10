package com.example.ddd.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 仓储接口基类
 * 领域层与基础设施层之间的契约（六边形架构的端口）
 *
 * @param <T> 实体类型
 * @author DDD Demo
 */
public interface BaseRepository<T> extends BaseMapper<T> {

    /**
     * 根据ID查找实体
     *
     * @param id ID
     * @return 实体对象
     */
    default T findById(Long id) {
        return selectById(id);
    }

    /**
     * 保存实体
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    default int save(T entity) {
        return insert(entity);
    }

    /**
     * 更新实体
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    default int update(T entity) {
        return updateById(entity);
    }

    /**
     * 删除实体
     *
     * @param id ID
     * @return 影响行数
     */
    default int delete(Long id) {
        return deleteById(id);
    }
}
