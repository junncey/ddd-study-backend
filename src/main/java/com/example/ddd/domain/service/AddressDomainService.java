package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Address;
import com.example.ddd.domain.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 地址领域服务
 * 包含地址相关的核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressDomainService extends DomainService {

    private final AddressRepository addressRepository;

    /**
     * 创建地址
     * 如果设置为默认地址，则清除其他地址的默认标记
     *
     * @param address 地址实体
     * @return 创建后的地址
     */
    @Transactional(rollbackFor = Exception.class)
    public Address createAddress(Address address) {
        validate();

        // 如果设置为默认地址，清除其他地址的默认标记
        if (address.isDefaultAddress()) {
            addressRepository.clearDefaultAddress(address.getUserId());
        }

        return addressRepository.save(address);
    }

    /**
     * 设置默认地址
     * 清除其他地址的默认标记，并将指定地址设为默认
     *
     * @param addressId 地址ID
     * @param userId   用户ID
     * @return 更新后的地址
     */
    @Transactional(rollbackFor = Exception.class)
    public Address setDefault(Long addressId, Long userId) {
        validate();

        // 验证地址是否属于该用户
        Address address = addressRepository.findById(addressId);
        if (address == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此地址");
        }

        // 清除其他地址的默认标记
        addressRepository.clearDefaultAddress(userId);

        // 设置为默认地址
        address.setAsDefault();
        addressRepository.update(address);

        return address;
    }

    /**
     * 更新地址
     *
     * @param address 地址实体
     * @return 更新后的地址
     */
    @Transactional(rollbackFor = Exception.class)
    public Address updateAddress(Address address) {
        validate();

        // 验证地址是否存在
        Address existing = addressRepository.findById(address.getId());
        if (existing == null) {
            throw new IllegalArgumentException("地址不存在");
        }

        // 如果设置为默认地址，清除其他地址的默认标记
        if (address.isDefaultAddress() && !existing.isDefaultAddress()) {
            addressRepository.clearDefaultAddress(address.getUserId());
        }

        return addressRepository.save(address);
    }

    /**
     * 删除地址
     *
     * @param addressId 地址ID
     * @param userId   用户ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAddress(Long addressId, Long userId) {
        validate();

        // 验证地址是否属于该用户
        Address address = addressRepository.findById(addressId);
        if (address == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此地址");
        }

        return addressRepository.delete(addressId) > 0;
    }
}
