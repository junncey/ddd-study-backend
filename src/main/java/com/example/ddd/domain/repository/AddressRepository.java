package com.example.ddd.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Address;

import java.util.List;

/**
 * 地址仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface AddressRepository extends BaseRepository<Address> {

    /**
     * 根据用户ID查询地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> findByUserId(Long userId);

    /**
     * 根据用户ID查询默认地址
     *
     * @param userId 用户ID
     * @return 默认地址，如果没有则返回 null
     */
    Address findDefaultByUserId(Long userId);

    /**
     * 清除用户的所有默认地址标记
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int clearDefaultAddress(Long userId);

    /**
     * 分页查询用户的地址
     *
     * @param page   分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<Address> pageByUserId(Page<Address> page, Long userId);
}
