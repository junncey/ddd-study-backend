package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Address;
import com.example.ddd.domain.repository.AddressRepository;
import com.example.ddd.infrastructure.persistence.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地址仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepository {

    private final AddressMapper addressMapper;

    @Override
    public Address findById(Long id) {
        return addressMapper.selectById(id);
    }

    @Override
    public Address save(Address entity) {
        if (entity.getId() == null) {
            addressMapper.insert(entity);
        } else {
            addressMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Address entity) {
        return addressMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return addressMapper.deleteById(id);
    }

    @Override
    public IPage<Address> page(Page<Address> page) {
        return addressMapper.selectPage(page, null);
    }

    @Override
    public List<Address> findByUserId(Long userId) {
        return addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime)
        );
    }

    @Override
    public Address findDefaultByUserId(Long userId) {
        return addressMapper.selectOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getIsDefault, 1)
        );
    }

    @Override
    public int clearDefaultAddress(Long userId) {
        return addressMapper.update(null,
                new LambdaUpdateWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .set(Address::getIsDefault, 0)
        );
    }

    @Override
    public IPage<Address> pageByUserId(Page<Address> page, Long userId) {
        return addressMapper.selectPage(page,
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime)
        );
    }
}
