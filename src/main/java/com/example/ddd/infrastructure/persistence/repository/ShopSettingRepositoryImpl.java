package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.ShopSetting;
import com.example.ddd.domain.repository.ShopSettingRepository;
import com.example.ddd.infrastructure.persistence.mapper.ShopSettingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺设置仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class ShopSettingRepositoryImpl implements ShopSettingRepository {

    private final ShopSettingMapper shopSettingMapper;

    @Override
    public ShopSetting findById(Long id) {
        return shopSettingMapper.selectById(id);
    }

    @Override
    public ShopSetting save(ShopSetting entity) {
        if (entity.getId() == null) {
            shopSettingMapper.insert(entity);
        } else {
            shopSettingMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(ShopSetting entity) {
        return shopSettingMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return shopSettingMapper.deleteById(id);
    }

    @Override
    public IPage<ShopSetting> page(Page<ShopSetting> page) {
        return shopSettingMapper.selectPage(page, null);
    }

    @Override
    public List<ShopSetting> findByShopId(Long shopId) {
        return shopSettingMapper.selectList(
                new LambdaQueryWrapper<ShopSetting>()
                        .eq(ShopSetting::getShopId, shopId)
        );
    }

    @Override
    public ShopSetting findByShopIdAndKey(Long shopId, String settingKey) {
        return shopSettingMapper.selectOne(
                new LambdaQueryWrapper<ShopSetting>()
                        .eq(ShopSetting::getShopId, shopId)
                        .eq(ShopSetting::getSettingKey, settingKey)
        );
    }

    @Override
    public Map<String, String> findSettingsAsMap(Long shopId) {
        List<ShopSetting> settings = findByShopId(shopId);
        Map<String, String> map = new HashMap<>();
        for (ShopSetting setting : settings) {
            map.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopSetting saveOrUpdate(ShopSetting shopSetting) {
        ShopSetting existing = findByShopIdAndKey(shopSetting.getShopId(), shopSetting.getSettingKey());
        if (existing != null) {
            shopSetting.setId(existing.getId());
            update(shopSetting);
        } else {
            save(shopSetting);
        }
        return shopSetting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long shopId, Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            ShopSetting shopSetting = new ShopSetting();
            shopSetting.setShopId(shopId);
            shopSetting.setSettingKey(entry.getKey());
            shopSetting.setSettingValue(entry.getValue());
            saveOrUpdate(shopSetting);
        }
    }
}
