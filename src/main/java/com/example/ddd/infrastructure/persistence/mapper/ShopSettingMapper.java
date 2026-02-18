package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.ShopSetting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺设置 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface ShopSettingMapper extends BaseMapper<ShopSetting> {
}
