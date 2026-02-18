package com.example.ddd.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Address;
import com.example.ddd.domain.repository.AddressRepository;
import com.example.ddd.domain.service.AddressDomainService;
import com.example.ddd.interfaces.rest.dto.AddressCreateRequest;
import com.example.ddd.interfaces.rest.dto.AddressResponse;
import com.example.ddd.interfaces.rest.dto.AddressUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地址应用服务
 * 编排地址相关的用例
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressApplicationService extends ApplicationService {

    private final AddressDomainService addressDomainService;
    private final AddressRepository addressRepository;

    /**
     * 创建地址用例
     *
     * @param userId  用户ID（从认证上下文获取）
     * @param request 创建请求
     * @return 地址响应
     */
    public AddressResponse createAddress(Long userId, AddressCreateRequest request) {
        beforeExecute();
        try {
            Address address = new Address();
            address.setUserId(userId);
            address.setReceiverName(request.getReceiverName());
            address.setReceiverPhone(request.getReceiverPhone());
            address.setProvince(request.getProvince());
            address.setCity(request.getCity());
            address.setDistrict(request.getDistrict());
            address.setDetailAddress(request.getDetailAddress());
            address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);

            Address created = addressDomainService.createAddress(address);
            return toResponse(created);
        } finally {
            afterExecute();
        }
    }

    /**
     * 更新地址用例
     *
     * @param userId  用户ID（从认证上下文获取）
     * @param request 更新请求
     * @return 地址响应
     */
    public AddressResponse updateAddress(Long userId, AddressUpdateRequest request) {
        beforeExecute();
        try {
            Address address = new Address();
            address.setId(request.getId());
            address.setUserId(userId);
            address.setReceiverName(request.getReceiverName());
            address.setReceiverPhone(request.getReceiverPhone());
            address.setProvince(request.getProvince());
            address.setCity(request.getCity());
            address.setDistrict(request.getDistrict());
            address.setDetailAddress(request.getDetailAddress());
            address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);

            Address updated = addressDomainService.updateAddress(address);
            return toResponse(updated);
        } finally {
            afterExecute();
        }
    }

    /**
     * 删除地址用例
     *
     * @param addressId 地址ID
     * @param userId    用户ID
     * @return 是否删除成功
     */
    public boolean deleteAddress(Long addressId, Long userId) {
        beforeExecute();
        try {
            return addressDomainService.deleteAddress(addressId, userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 查询地址详情
     *
     * @param addressId 地址ID
     * @return 地址响应
     */
    public AddressResponse getAddressById(Long addressId) {
        beforeExecute();
        try {
            Address address = addressRepository.findById(addressId);
            return toResponse(address);
        } finally {
            afterExecute();
        }
    }

    /**
     * 查询用户的所有地址
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        beforeExecute();
        try {
            List<Address> addresses = addressRepository.findByUserId(userId);
            return addresses.stream().map(this::toResponse).toList();
        } finally {
            afterExecute();
        }
    }

    /**
     * 查询用户的默认地址
     *
     * @param userId 用户ID
     * @return 地址响应
     */
    public AddressResponse getDefaultAddress(Long userId) {
        beforeExecute();
        try {
            Address address = addressRepository.findDefaultByUserId(userId);
            return toResponse(address);
        } finally {
            afterExecute();
        }
    }

    /**
     * 设置默认地址
     *
     * @param addressId 地址ID
     * @param userId    用户ID
     * @return 地址响应
     */
    public AddressResponse setDefaultAddress(Long addressId, Long userId) {
        beforeExecute();
        try {
            Address address = addressDomainService.setDefault(addressId, userId);
            return toResponse(address);
        } finally {
            afterExecute();
        }
    }

    /**
     * 分页查询用户的地址
     *
     * @param current 当前页
     * @param size    每页大小
     * @param userId  用户ID
     * @return 分页结果
     */
    public IPage<AddressResponse> pageAddressesByUserId(Long current, Long size, Long userId) {
        beforeExecute();
        try {
            IPage<Address> page = addressRepository.pageByUserId(new Page<>(current, size), userId);
            return page.convert(this::toResponse);
        } finally {
            afterExecute();
        }
    }

    /**
     * 转换为响应 DTO
     *
     * @param address 地址实体
     * @return 地址响应
     */
    private AddressResponse toResponse(Address address) {
        if (address == null) {
            return null;
        }
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setUserId(address.getUserId());
        response.setReceiverName(address.getReceiverName());
        response.setReceiverPhone(address.getReceiverPhone());
        response.setProvince(address.getProvince());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setDetailAddress(address.getDetailAddress());
        response.setFullAddress(address.getFullAddress());
        response.setIsDefault(address.getIsDefault());
        response.setCreateTime(address.getCreateTime());
        response.setUpdateTime(address.getUpdateTime());
        return response;
    }
}
