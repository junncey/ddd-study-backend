package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.FileInfo;
import com.example.ddd.domain.model.valueobject.BizType;

import java.util.List;
import java.util.Optional;

/**
 * 文件仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface FileRepository extends BaseRepository<FileInfo> {

    /**
     * 根据fileKey查询文件
     *
     * @param fileKey 文件唯一标识
     * @return 文件信息
     */
    Optional<FileInfo> findByFileKey(String fileKey);

    /**
     * 查询业务关联的所有文件
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @return 文件列表
     */
    List<FileInfo> findByBizTypeAndBizId(BizType bizType, Long bizId);

    /**
     * 查询业务关联的第一个文件（主图）
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @return 文件信息
     */
    Optional<FileInfo> findFirstByBizTypeAndBizId(BizType bizType, Long bizId);

    /**
     * 查询过期的待绑定文件
     *
     * @return 过期文件列表
     */
    List<FileInfo> findExpiredPendingFiles();

    /**
     * 根据文件哈希查询（用于去重）
     *
     * @param fileHash 文件MD5哈希
     * @return 文件信息
     */
    Optional<FileInfo> findByFileHash(String fileHash);

    /**
     * 根据fileKey删除文件
     *
     * @param fileKey 文件唯一标识
     * @return 影响行数
     */
    int deleteByFileKey(String fileKey);

    /**
     * 批量删除业务关联的文件（逻辑删除）
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @return 影响行数
     */
    int deleteByBizTypeAndBizId(BizType bizType, Long bizId);
}
