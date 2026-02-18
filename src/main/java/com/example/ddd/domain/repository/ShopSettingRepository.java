package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.ShopSetting;

import java.util.List;
import java.util.Map;

/**
 * 店铺设置仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface ShopSettingRepository extends BaseRepository<ShopSetting> {

    /**
     * 根据店铺ID查询所有设置
     *
     * @param shopId 店铺ID
     * @return 设置列表
     */
    List<ShopSetting> findByShopId(Long shopId);

    /**
     * 根据店铺ID和设置键查询设置
     *
     * @param shopId     店铺ID
     * @param settingKey 设置键
     * @return 设置对象
     */
    ShopSetting findByShopIdAndKey(Long shopId, String settingKey);

    /**
     * 根据店铺ID查询设置Map
     *
     * @param shopId 店铺ID
     * @return 设置Map
     */
    Map<String, String> findSettingsAsMap(Long shopId);

    /**
     * 保存或更新设置
     *
     * @param shopSetting 设置对象
     * @return 保存后的设置
     */
    ShopSetting saveOrUpdate(ShopSetting shopSetting);

    /**
     * 批量保存店铺设置
     *
     * @param shopId   店铺ID
     * @param settings 设置Map
     */
    void batchSave(Long shopId, Map<String, String> settings);
}
