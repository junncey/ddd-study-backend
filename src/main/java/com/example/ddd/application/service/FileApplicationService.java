package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.FileInfo;
import com.example.ddd.domain.model.valueobject.BizType;
import com.example.ddd.domain.model.valueobject.FileStatus;
import com.example.ddd.domain.model.valueobject.StorageType;
import com.example.ddd.domain.repository.FileRepository;
import com.example.ddd.infrastructure.storage.StorageProperties;
import com.example.ddd.infrastructure.storage.StorageService;
import com.example.ddd.interfaces.rest.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件应用服务
 * 处理文件上传、绑定、查询等业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileApplicationService extends ApplicationService {

    private final FileRepository fileRepository;
    private final StorageService storageService;
    private final StorageProperties storageProperties;

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 上传文件（待绑定状态）
     *
     * @param file       上传的文件
     * @param uploaderId 上传者ID
     * @return 上传响应
     */
    @Transactional
    public FileUploadResponse upload(MultipartFile file, Long uploaderId) {
        return upload(file, uploaderId, null);
    }

    /**
     * 上传文件并指定业务类型
     *
     * @param file       上传的文件
     * @param uploaderId 上传者ID
     * @param bizType    业务类型
     * @return 上传响应
     */
    @Transactional
    public FileUploadResponse upload(MultipartFile file, Long uploaderId, BizType bizType) {
        // 1. 校验文件
        validateFile(file);

        // 2. 生成文件信息
        String fileKey = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String datePath = LocalDateTime.now().format(DATE_PATH_FORMATTER);
        String storageFilename = fileKey + "." + extension;

        // 3. 计算文件哈希（用于去重）
        String fileHash = calculateFileHash(file);

        // 4. 检查是否已存在相同文件（可选的去重逻辑）
        // fileRepository.findByFileHash(fileHash).ifPresent(existingFile -> {
        //     // 如果文件已存在，直接返回已有记录
        //     return toResponse(existingFile);
        // });

        // 5. 存储文件
        String storagePath;
        try {
            storagePath = storageService.store(file, datePath, storageFilename);
        } catch (Exception e) {
            log.error("文件存储失败: {}", originalFilename, e);
            throw new RuntimeException("文件存储失败: " + e.getMessage());
        }

        // 6. 构建访问URL
        String accessUrl = storageService.getAccessUrl(storagePath);

        // 7. 保存文件元数据
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileKey(fileKey);
        fileInfo.setFileName(originalFilename);
        fileInfo.setStoragePath(storagePath);
        fileInfo.setStorageType(StorageType.fromName(storageService.getStorageType()));
        fileInfo.setFileSize(file.getSize());
        fileInfo.setContentType(file.getContentType());
        fileInfo.setFileHash(fileHash);
        fileInfo.setAccessUrl(accessUrl);
        fileInfo.setBizType(bizType);
        fileInfo.setUploaderId(uploaderId);
        fileInfo.setStatus(FileStatus.PENDING);
        fileInfo.setExpireTime(LocalDateTime.now().plusDays(storageProperties.getPendingExpireDays()));

        fileRepository.save(fileInfo);

        log.info("文件上传成功: fileKey={}, path={}, size={}", fileKey, storagePath, file.getSize());

        return toResponse(fileInfo);
    }

    /**
     * 绑定文件到业务
     *
     * @param fileKey 文件唯一标识
     * @param bizType 业务类型
     * @param bizId   业务ID
     */
    @Transactional
    public void bindToBusiness(String fileKey, BizType bizType, Long bizId) {
        FileInfo fileInfo = fileRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new RuntimeException("文件不存在: " + fileKey));

        // 如果文件已经绑定到相同的业务对象，跳过绑定（幂等操作）
        if (fileInfo.isBound() && fileInfo.getBizType() == bizType
                && bizId != null && bizId.equals(fileInfo.getBizId())) {
            log.info("文件已绑定到当前业务，跳过绑定: fileKey={}, bizType={}, bizId={}", fileKey, bizType, bizId);
            return;
        }

        // 如果文件绑定到不同的业务，报错
        if (fileInfo.isBound()) {
            throw new RuntimeException("文件已绑定到其他业务，无法重复绑定: " + fileKey);
        }

        // 只有PENDING状态的文件才能绑定
        if (!fileInfo.isPending()) {
            throw new RuntimeException("文件状态不正确，无法绑定: " + fileInfo.getStatus());
        }

        fileInfo.bindBusiness(bizType, bizId, storageProperties.getBoundExpireDays());
        fileRepository.save(fileInfo);

        log.info("文件绑定成功: fileKey={}, bizType={}, bizId={}", fileKey, bizType, bizId);
    }

    /**
     * 批量绑定文件到业务
     *
     * @param fileKeys 文件唯一标识列表
     * @param bizType  业务类型
     * @param bizId    业务ID
     */
    @Transactional
    public void bindToBusiness(List<String> fileKeys, BizType bizType, Long bizId) {
        for (String fileKey : fileKeys) {
            bindToBusiness(fileKey, bizType, bizId);
        }
    }

    /**
     * 获取业务关联的文件列表
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @return 文件列表
     */
    public List<FileUploadResponse> getFilesByBusiness(BizType bizType, Long bizId) {
        return fileRepository.findByBizTypeAndBizId(bizType, bizId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取业务关联的主图（第一个文件）
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @return 文件信息
     */
    public FileUploadResponse getMainImage(BizType bizType, Long bizId) {
        return fileRepository.findFirstByBizTypeAndBizId(bizType, bizId)
                .map(this::toResponse)
                .orElse(null);
    }

    /**
     * 根据fileKey获取文件信息
     *
     * @param fileKey 文件唯一标识
     * @return 文件信息
     */
    public FileUploadResponse getByFileKey(String fileKey) {
        return fileRepository.findByFileKey(fileKey)
                .map(this::toResponse)
                .orElse(null);
    }

    /**
     * 删除文件
     *
     * @param fileKey 文件唯一标识
     */
    @Transactional
    public void deleteFile(String fileKey) {
        FileInfo fileInfo = fileRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new RuntimeException("文件不存在: " + fileKey));

        // 标记删除（不立即删除物理文件，由定时任务清理）
        fileInfo.markDeleted(storageProperties.getPendingExpireDays());
        fileRepository.save(fileInfo);

        log.info("文件标记删除成功: fileKey={}, expireTime={}", fileKey, fileInfo.getExpireTime());
    }

    /**
     * 删除业务关联的所有文件
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     */
    @Transactional
    public void deleteByBusiness(BizType bizType, Long bizId) {
        List<FileInfo> files = fileRepository.findByBizTypeAndBizId(bizType, bizId);
        for (FileInfo file : files) {
            file.markDeleted(storageProperties.getPendingExpireDays());
            fileRepository.save(file);
        }
        log.info("业务关联文件标记删除完成: bizType={}, bizId={}, count={}", bizType, bizId, files.size());
    }

    /**
     * 清理过期文件（临时文件和已删除文件）
     * 数据库逻辑删除，磁盘文件物理删除
     *
     * @return 清理的文件数量
     */
    @Transactional
    public int cleanExpiredFiles() {
        List<FileInfo> expiredFiles = fileRepository.findExpiredFiles();
        int count = 0;
        for (FileInfo file : expiredFiles) {
            try {
                // 物理删除磁盘文件
                storageService.delete(file.getStoragePath());
                log.info("物理文件删除成功: {}", file.getStoragePath());
            } catch (Exception e) {
                log.warn("物理文件删除失败: {}", file.getStoragePath(), e);
            }

            // 逻辑删除数据库记录
            fileRepository.delete(file.getId());
            count++;
        }
        log.info("清理过期文件完成: {}个", count);
        return count;
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 校验文件大小
        if (file.getSize() > storageProperties.getMaxSize()) {
            throw new IllegalArgumentException("文件大小超过限制（最大" + (storageProperties.getMaxSize() / 1024 / 1024) + "MB）");
        }

        // 校验文件类型
        String extension = getFileExtension(file.getOriginalFilename());
        if (!storageProperties.isAllowedType(extension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + extension);
        }
    }

    /**
     * 计算文件MD5哈希
     */
    private String calculateFileHash(MultipartFile file) {
        try {
            return DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            log.warn("计算文件哈希失败", e);
            return null;
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 转换为响应DTO
     */
    private FileUploadResponse toResponse(FileInfo fileInfo) {
        return FileUploadResponse.builder()
                .fileKey(fileInfo.getFileKey())
                .url(fileInfo.getAccessUrl())
                .fileName(fileInfo.getFileName())
                .fileSize(fileInfo.getFileSize())
                .build();
    }
}
