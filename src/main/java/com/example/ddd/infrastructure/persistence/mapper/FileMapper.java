package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件 Mapper
 * 六边形架构的适配器，实现领域层定义的 FileRepository 接口
 *
 * @author DDD Demo
 */
@Mapper
public interface FileMapper extends BaseMapper<FileInfo> {
}
