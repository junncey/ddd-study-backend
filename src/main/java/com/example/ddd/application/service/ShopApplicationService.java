package com.example.ddd.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Shop;
import com.example.ddd.domain.model.valueobject.ShopStatus;
import com.example.ddd.domain.repository.ShopRepository;
import com.example.ddd.domain.service.ShopDomainService;
import com.example.ddd.interfaces.rest.dto.ShopApproveRequest;
import com.example.ddd.interfaces.rest.dto.ShopCreateRequest;
import com.example.ddd.interfaces.rest.dto.ShopResponse;
import com.example.ddd.interfaces.rest.dto.ShopUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 店铺应用服务
 * 编排店铺相关的用例
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopApplicationService extends ApplicationService {

    private final ShopDomainService shopDomainService;
    private final ShopRepository shopRepository;

    /**
     * 注册店铺用例
     *
     * @param request 创建请求
     * @return 店铺响应
     */
    public ShopResponse registerShop(ShopCreateRequest request) {
        beforeExecute();
        try {
            Shop shop = new Shop();
            shop.setShopName(request.getShopName());
            shop.setShopLogo(request.getShopLogo());
            shop.setDescription(request.getDescription());
            shop.setOwnerId(request.getOwnerId());

            Shop created = shopDomainService.registerShop(shop);
            return toResponse(created);
        } finally {
            afterExecute();
        }
    }

    /**
     * 更新店铺用例
     *
     * @param request 更新请求
     * @return 店铺响应
     */
    public ShopResponse updateShop(ShopUpdateRequest request) {
        beforeExecute();
        try {
            Shop shop = new Shop();
            shop.setId(request.getId());
            shop.setShopName(request.getShopName());
            shop.setShopLogo(request.getShopLogo());
            shop.setDescription(request.getDescription());

            Shop updated = shopDomainService.updateShop(shop);
            return toResponse(updated);
        } finally {
            afterExecute();
        }
    }

    /**
     * 审核店铺用例
     *
     * @param request 审核请求
     * @return 店铺响应
     */
    public ShopResponse approveShop(ShopApproveRequest request) {
        beforeExecute();
        try {
            ShopStatus status = ShopStatus.fromValue(request.getStatus());
            Shop updated = shopDomainService.approveShop(request.getShopId(), status);
            return toResponse(updated);
        } finally {
            afterExecute();
        }
    }

    /**
     * 暂停店铺用例
     *
     * @param shopId 店铺ID
     * @return 店铺响应
     */
    public ShopResponse suspendShop(Long shopId) {
        beforeExecute();
        try {
            Shop updated = shopDomainService.suspendShop(shopId);
            return toResponse(updated);
        } finally {
            afterExecute();
        }
    }

    /**
     * 恢复店铺用例
     *
     * @param shopId 店铺ID
     * @return 店铺响应
     */
    public ShopResponse resumeShop(Long shopId) {
        beforeExecute();
        try {
            Shop updated = shopDomainService.resumeShop(shopId);
            return toResponse(updated);
        } finally {
            afterExecute();
        }
    }

    /**
     * 关闭店铺用例
     *
     * @param shopId 店铺ID
     * @return 店铺响应
     */
    public ShopResponse closeShop(Long shopId) {
        beforeExecute();
        try {
            Shop updated = shopDomainService.closeShop(shopId);
            return toResponse(updated);
        } finally {
            afterExecute();
        }
    }

    /**
     * 查询店铺详情
     *
     * @param shopId 店铺ID
     * @return 店铺响应
     */
    public ShopResponse getShopById(Long shopId) {
        beforeExecute();
        try {
            Shop shop = shopRepository.findById(shopId);
            return toResponse(shop);
        } finally {
            afterExecute();
        }
    }

    /**
     * 根据店主ID查询店铺
     *
     * @param ownerId 店主ID
     * @return 店铺响应
     */
    public ShopResponse getShopByOwnerId(Long ownerId) {
        beforeExecute();
        try {
            Shop shop = shopRepository.findByOwnerId(ownerId);
            return toResponse(shop);
        } finally {
            afterExecute();
        }
    }

    /**
     * 查询所有店铺
     *
     * @param status 状态（可选）
     * @return 店铺列表
     */
    public List<ShopResponse> getAllShops(Integer status) {
        beforeExecute();
        try {
            List<Shop> shops;
            if (status == null) {
                shops = shopRepository.page(new Page<>(1, 1000), null).getRecords();
            } else {
                ShopStatus shopStatus = ShopStatus.fromValue(status);
                shops = shopRepository.findByStatus(shopStatus);
            }
            return shops.stream().map(this::toResponse).toList();
        } finally {
            afterExecute();
        }
    }

    /**
     * 分页查询店铺
     *
     * @param current 当前页
     * @param size    每页大小
     * @param status  状态（可选）
     * @return 分页结果
     */
    public IPage<ShopResponse> pageShops(Long current, Long size, Integer status) {
        beforeExecute();
        try {
            ShopStatus shopStatus = status != null ? ShopStatus.fromValue(status) : null;
            IPage<Shop> page = shopRepository.page(new Page<>(current, size), shopStatus);
            return page.convert(this::toResponse);
        } finally {
            afterExecute();
        }
    }

    /**
     * 转换为响应 DTO
     *
     * @param shop 店铺实体
     * @return 店铺响应
     */
    private ShopResponse toResponse(Shop shop) {
        if (shop == null) {
            return null;
        }
        ShopResponse response = new ShopResponse();
        response.setId(shop.getId());
        response.setShopName(shop.getShopName());
        response.setShopLogo(shop.getShopLogo());
        response.setDescription(shop.getDescription());
        response.setOwnerId(shop.getOwnerId());
        response.setStatus(shop.getStatusInt());
        response.setStatusDesc(shop.getStatus() != null ? shop.getStatus().getDescription() : null);
        response.setCreateTime(shop.getCreateTime());
        response.setUpdateTime(shop.getUpdateTime());
        return response;
    }
}
