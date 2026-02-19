package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.FileInfo;
import com.example.ddd.domain.model.valueobject.BizType;
import com.example.ddd.domain.model.valueobject.FileStatus;
import com.example.ddd.domain.repository.FileRepository;
import com.example.ddd.infrastructure.persistence.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 文件仓储实现
 * 六边形架构的适配器，实现领域层定义的端口
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private final FileMapper fileMapper;

    @Override
    public FileInfo findById(Long id) {
        return fileMapper.selectById(id);
    }

    @Override
    public FileInfo save(FileInfo entity) {
        if (entity.getId() == null) {
            fileMapper.insert(entity);
        } else {
            fileMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(FileInfo entity) {
        return fileMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return fileMapper.deleteById(id);
    }

    @Override
    public IPage<FileInfo> page(Page<FileInfo> page) {
        return fileMapper.selectPage(page, null);
    }

    @Override
    public Optional<FileInfo> findByFileKey(String fileKey) {
        FileInfo fileInfo = fileMapper.selectOne(
                new LambdaQueryWrapper<FileInfo>()
                        .eq(FileInfo::getFileKey, fileKey)
        );
        return Optional.ofNullable(fileInfo);
    }

    @Override
    public List<FileInfo> findByBizTypeAndBizId(BizType bizType, Long bizId) {
        return fileMapper.selectList(
                new LambdaQueryWrapper<FileInfo>()
                        .eq(FileInfo::getBizType, bizType)
                        .eq(FileInfo::getBizId, bizId)
                        .orderByAsc(FileInfo::getCreateTime)
        );
    }

    @Override
    public Optional<FileInfo> findFirstByBizTypeAndBizId(BizType bizType, Long bizId) {
        FileInfo fileInfo = fileMapper.selectOne(
                new LambdaQueryWrapper<FileInfo>()
                        .eq(FileInfo::getBizType, bizType)
                        .eq(FileInfo::getBizId, bizId)
                        .orderByAsc(FileInfo::getCreateTime)
                        .last("LIMIT 1")
        );
        return Optional.ofNullable(fileInfo);
    }

    @Override
    public List<FileInfo> findExpiredPendingFiles() {
        return fileMapper.selectList(
                new LambdaQueryWrapper<FileInfo>()
                        .eq(FileInfo::getStatus, FileStatus.PENDING)
                        .lt(FileInfo::getExpireTime, LocalDateTime.now())
        );
    }

    @Override
    public Optional<FileInfo> findByFileHash(String fileHash) {
        FileInfo fileInfo = fileMapper.selectOne(
                new LambdaQueryWrapper<FileInfo>()
                        .eq(FileInfo::getFileHash, fileHash)
        );
        return Optional.ofNullable(fileInfo);
    }

    @Override
    public int deleteByFileKey(String fileKey) {
        return fileMapper.delete(
                new LambdaQueryWrapper<FileInfo>()
                        .eq(FileInfo::getFileKey, fileKey)
        );
    }

    @Override
    public int deleteByBizTypeAndBizId(BizType bizType, Long bizId) {
        return fileMapper.update(null,
                new LambdaUpdateWrapper<FileInfo>()
                        .set(FileInfo::getStatus, FileStatus.DELETED)
                        .eq(FileInfo::getBizType, bizType)
                        .eq(FileInfo::getBizId, bizId)
        );
    }
}
