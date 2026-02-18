package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Shop;
import com.example.ddd.domain.model.valueobject.ShopStatus;
import com.example.ddd.domain.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 店铺领域服务
 * 包含店铺相关的核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopDomainService extends DomainService {

    private final ShopRepository shopRepository;

    /**
     * 注册店铺
     * 验证店主是否已有店铺，然后创建新店铺
     *
     * @param shop 店铺实体
     * @return 创建后的店铺
     */
    @Transactional(rollbackFor = Exception.class)
    public Shop registerShop(Shop shop) {
        validate();

        // 检查店主是否已有店铺
        if (shopRepository.existsByOwnerId(shop.getOwnerId())) {
            throw new IllegalArgumentException("该用户已拥有店铺，不能重复注册");
        }

        // 设置初始状态为待审核
        shop.setStatus(ShopStatus.PENDING);

        return shopRepository.save(shop);
    }

    /**
     * 审核店铺
     *
     * @param shopId 店铺ID
     * @param status 审核结果（通过/拒绝）
     * @return 更新后的店铺
     */
    @Transactional(rollbackFor = Exception.class)
    public Shop approveShop(Long shopId, ShopStatus status) {
        validate();

        Shop shop = shopRepository.findById(shopId);
        if (shop == null) {
            throw new IllegalArgumentException("店铺不存在");
        }

        // 只有待审核状态才能审核
        if (!shop.isPending()) {
            throw new IllegalArgumentException("只有待审核状态的店铺才能审核");
        }

        // 验证状态流转
        if (!shop.getStatus().canTransitionTo(status)) {
            throw new IllegalArgumentException("不允许从 " + shop.getStatus().getDescription()
                    + " 变更为 " + status.getDescription());
        }

        shop.setStatus(status);
        return shopRepository.update(shop) > 0 ? shop : null;
    }

    /**
     * 更新店铺信息
     *
     * @param shop 店铺实体
     * @return 更新后的店铺
     */
    @Transactional(rollbackFor = Exception.class)
    public Shop updateShop(Shop shop) {
        validate();

        // 验证店铺是否存在
        Shop existing = shopRepository.findById(shop.getId());
        if (existing == null) {
            throw new IllegalArgumentException("店铺不存在");
        }

        // 只有已审核状态才能更新基本信息
        if (!existing.canOperate()) {
            throw new IllegalArgumentException("只有已审核的店铺才能更新信息");
        }

        return shopRepository.save(shop);
    }

    /**
     * 暂停店铺
     *
     * @param shopId 店铺ID
     * @return 更新后的店铺
     */
    @Transactional(rollbackFor = Exception.class)
    public Shop suspendShop(Long shopId) {
        validate();

        Shop shop = shopRepository.findById(shopId);
        if (shop == null) {
            throw new IllegalArgumentException("店铺不存在");
        }

        if (!shop.getStatus().canSuspend()) {
            throw new IllegalArgumentException("当前状态不允许暂停店铺");
        }

        shop.setStatus(ShopStatus.SUSPENDED);
        return shopRepository.update(shop) > 0 ? shop : null;
    }

    /**
     * 恢复店铺
     *
     * @param shopId 店铺ID
     * @return 更新后的店铺
     */
    @Transactional(rollbackFor = Exception.class)
    public Shop resumeShop(Long shopId) {
        validate();

        Shop shop = shopRepository.findById(shopId);
        if (shop == null) {
            throw new IllegalArgumentException("店铺不存在");
        }

        if (shop.getStatus() != ShopStatus.SUSPENDED) {
            throw new IllegalArgumentException("只有暂停状态的店铺才能恢复");
        }

        shop.setStatus(ShopStatus.APPROVED);
        return shopRepository.update(shop) > 0 ? shop : null;
    }

    /**
     * 关闭店铺
     *
     * @param shopId 店铺ID
     * @return 更新后的店铺
     */
    @Transactional(rollbackFor = Exception.class)
    public Shop closeShop(Long shopId) {
        validate();

        Shop shop = shopRepository.findById(shopId);
        if (shop == null) {
            throw new IllegalArgumentException("店铺不存在");
        }

        if (!shop.getStatus().canClose()) {
            throw new IllegalArgumentException("当前状态不允许关闭店铺");
        }

        shop.setStatus(ShopStatus.CLOSED);
        return shopRepository.update(shop) > 0 ? shop : null;
    }
}
