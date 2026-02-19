# 扩展性文件管理方案设计

## 日期
2026-02-19

## 状态
✅ 已实施完成

## 需求说明
1. 支持本地存储和云存储（OSS/COS/S3等）的灵活切换
2. 所有文件需要独立的文件表存储元数据
3. 通过文件表的 biz_type 和 biz_id 字段绑定业务，业务表不需要存储文件相关字段

---

## 实施完成情况

### 已创建的文件

#### 领域层
- ✅ `domain/model/entity/FileInfo.java` - 文件实体
- ✅ `domain/model/valueobject/StorageType.java` - 存储类型枚举
- ✅ `domain/model/valueobject/BizType.java` - 业务类型枚举
- ✅ `domain/model/valueobject/FileStatus.java` - 文件状态枚举
- ✅ `domain/repository/FileRepository.java` - 文件仓储接口

#### 基础设施层
- ✅ `infrastructure/persistence/mapper/FileMapper.java` - MyBatis Mapper
- ✅ `infrastructure/persistence/repository/FileRepositoryImpl.java` - 仓储实现
- ✅ `infrastructure/persistence/handler/StorageTypeHandler.java` - 存储类型TypeHandler
- ✅ `infrastructure/persistence/handler/BizTypeHandler.java` - 业务类型TypeHandler
- ✅ `infrastructure/persistence/handler/FileStatusHandler.java` - 文件状态TypeHandler
- ✅ `infrastructure/storage/StorageService.java` - 存储服务接口
- ✅ `infrastructure/storage/LocalStorageService.java` - 本地存储实现
- ✅ `infrastructure/storage/StorageProperties.java` - 存储配置属性
- ✅ `infrastructure/config/WebConfig.java` - 静态资源配置

#### 应用层
- ✅ `application/service/FileApplicationService.java` - 文件应用服务

#### 接口层
- ✅ `interfaces/rest/controller/FileController.java` - 文件控制器
- ✅ `interfaces/rest/dto/FileUploadRequest.java` - 上传请求DTO
- ✅ `interfaces/rest/dto/FileUploadResponse.java` - 上传响应DTO

#### 数据库
- ✅ `db/migration/V1.0.2__file_storage_tables.sql` - 数据库迁移脚本

#### 配置文件
- ✅ `application.yml` - 新增文件存储配置

### 测试结果
- ✅ 编译通过
- ✅ 数据库迁移成功
- ✅ 服务启动成功
- ✅ 静态资源访问测试成功（http://localhost:8080/api/uploads/xxx）
- ✅ 文件上传接口需要认证（正确行为）

### 完整流程测试（2026-02-19 11:37）

| 步骤 | 操作 | 状态 |
|-----|------|------|
| 1 | 获取验证码 | ✅ 成功 |
| 2 | 管理员登录 | ✅ 成功 |
| 3 | 上传商品图片 | ✅ 成功 |
| 4 | 创建新商品 | ✅ 成功 (商品ID: 3) |
| 5 | 上架商品 | ✅ 成功 |
| 6 | 图片访问验证 | ✅ HTTP 200 |
| 7 | 商品列表查询 | ✅ 成功 |

### Bug 修复

1. **文件存储路径问题** - `LocalStorageService.java`
   - 问题：相对路径被解析到 Tomcat 临时目录
   - 修复：使用 `toAbsolutePath()` 获取绝对路径

2. **XSS过滤器冲突** - `XssFilter.java`
   - 问题：XssFilter 和 JsonXssFilter 都包装请求导致冲突
   - 修复：XssFilter 跳过 application/json 请求

3. **JsonXssFilter 编码问题** - `JsonXssFilter.java`
   - 问题：`request.getReader()` 导致编码异常
   - 修复：使用 `request.getInputStream().readAllBytes()` 读取请求体

4. **过滤器顺序** - `JsonXssFilter.java`
   - 问题：过滤器执行顺序不正确
   - 修复：将 JsonXssFilter 顺序改为 1

---

## 一、数据库设计

### 1. 文件表 (t_file)

```sql
CREATE TABLE t_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_key VARCHAR(255) NOT NULL COMMENT '文件唯一标识（UUID）',
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    storage_path VARCHAR(500) NOT NULL COMMENT '存储路径（相对路径）',
    storage_type VARCHAR(20) NOT NULL COMMENT '存储类型：LOCAL/OSS/COS/S3',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    content_type VARCHAR(100) COMMENT 'MIME类型',
    file_hash VARCHAR(64) COMMENT '文件MD5哈希（用于去重）',
    access_url VARCHAR(500) COMMENT '完整访问URL',
    biz_type VARCHAR(50) COMMENT '业务类型枚举',
    biz_id BIGINT COMMENT '关联的业务ID',
    uploader_id BIGINT COMMENT '上传者用户ID',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待绑定 1-已绑定 2-已删除',
    expire_time DATETIME COMMENT '过期时间（待绑定文件自动清理）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,

    UNIQUE KEY uk_file_key (file_key),
    INDEX idx_biz (biz_type, biz_id),
    INDEX idx_uploader (uploader_id),
    INDEX idx_expire (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件元数据表';
```

### 2. 业务类型枚举

```sql
-- 业务类型说明
-- PRODUCT_IMAGE: 商品图片
-- PRODUCT_DETAIL: 商品详情图
-- AVATAR: 用户头像
-- SHOP_LOGO: 店铺Logo
-- CATEGORY_ICON: 分类图标
```

---

## 二、后端代码结构

### 目录结构

```
src/main/java/com/example/ddd/
├── domain/
│   └── model/
│       ├── entity/
│       │   └── FileInfo.java              # 文件实体
│       ├── valueobject/
│       │   ├── StorageType.java           # 存储类型枚举
│       │   ├── BizType.java               # 业务类型枚举
│       │   └── FileStatus.java            # 文件状态枚举
│       └── repository/
│           └── FileRepository.java        # 文件仓储接口
│
├── application/
│   └── service/
│       └── FileApplicationService.java    # 文件应用服务
│
├── infrastructure/
│   ├── persistence/
│   │   ├── mapper/
│   │   │   └── FileMapper.java            # MyBatis Mapper
│   │   └── repository/
│   │       └── FileRepositoryImpl.java    # 仓储实现
│   ├── storage/
│   │   ├── StorageService.java            # 存储服务接口
│   │   ├── LocalStorageService.java       # 本地存储实现
│   │   ├── OssStorageService.java         # 阿里云OSS实现
│   │   └── StorageProperties.java         # 存储配置属性
│   └── config/
│       └── WebConfig.java                 # 静态资源配置
│
└── interfaces/
    ├── rest/
    │   ├── controller/
    │   │   └── FileController.java        # 文件接口
    │   └── dto/
    │       ├── FileUploadRequest.java     # 上传请求
    │       └── FileUploadResponse.java    # 上传响应
    └── job/
        └── TempFileCleanJob.java          # 临时文件清理任务
```

---

## 三、核心代码实现

### 1. 领域层 - 枚举定义

```java
// StorageType.java - 存储类型
package com.example.ddd.domain.model.valueobject;

public enum StorageType {
    LOCAL("本地存储"),
    OSS("阿里云OSS"),
    COS("腾讯云COS"),
    S3("AWS S3");

    private final String description;

    StorageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

// BizType.java - 业务类型
package com.example.ddd.domain.model.valueobject;

public enum BizType {
    PRODUCT_IMAGE("商品图片"),
    PRODUCT_DETAIL("商品详情图"),
    AVATAR("用户头像"),
    SHOP_LOGO("店铺Logo"),
    CATEGORY_ICON("分类图标");

    private final String description;

    BizType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

// FileStatus.java - 文件状态
package com.example.ddd.domain.model.valueobject;

public enum FileStatus {
    PENDING(0, "待绑定"),
    BOUND(1, "已绑定"),
    DELETED(2, "已删除");

    private final int code;
    private final String description;

    FileStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
```

### 2. 领域层 - 文件实体

```java
// FileInfo.java
package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.BizType;
import com.example.ddd.domain.model.valueobject.FileStatus;
import com.example.ddd.domain.model.valueobject.StorageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_file")
public class FileInfo extends BaseEntity {

    private String fileKey;
    private String fileName;
    private String storagePath;
    private StorageType storageType;
    private Long fileSize;
    private String contentType;
    private String fileHash;
    private String accessUrl;
    private BizType bizType;
    private Long bizId;
    private Long uploaderId;
    private FileStatus status;
    private LocalDateTime expireTime;

    /**
     * 绑定业务
     */
    public void bindBusiness(BizType bizType, Long bizId) {
        this.bizType = bizType;
        this.bizId = bizId;
        this.status = FileStatus.BOUND;
        this.expireTime = null;
    }

    /**
     * 标记删除
     */
    public void markDeleted() {
        this.status = FileStatus.DELETED;
    }

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }
}
```

### 3. 领域层 - 仓储接口

```java
// FileRepository.java
package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.FileInfo;
import com.example.ddd.domain.model.valueobject.BizType;
import com.example.ddd.domain.repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends BaseRepository<FileInfo> {

    /**
     * 根据fileKey查询
     */
    Optional<FileInfo> findByFileKey(String fileKey);

    /**
     * 查询业务关联的文件
     */
    List<FileInfo> findByBizTypeAndBizId(BizType bizType, Long bizId);

    /**
     * 查询业务关联的第一个文件（主图）
     */
    Optional<FileInfo> findFirstByBizTypeAndBizId(BizType bizType, Long bizId);

    /**
     * 查询过期的待绑定文件
     */
    List<FileInfo> findExpiredPendingFiles();

    /**
     * 根据文件哈希查询（用于去重）
     */
    Optional<FileInfo> findByFileHash(String fileHash);
}
```

### 4. 基础设施层 - 存储服务接口

```java
// StorageService.java
package com.example.ddd.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * 存储文件
     * @param file 上传的文件
     * @param path 相对存储路径
     * @return 实际存储路径
     */
    String store(MultipartFile file, String path) throws Exception;

    /**
     * 删除文件
     * @param path 文件路径
     */
    void delete(String path) throws Exception;

    /**
     * 获取访问URL
     * @param path 文件路径
     * @return 完整访问URL
     */
    String getAccessUrl(String path);

    /**
     * 获取存储类型
     */
    String getStorageType();
}
```

### 5. 基础设施层 - 本地存储实现

```java
// LocalStorageService.java
package com.example.ddd.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class LocalStorageService implements StorageService {

    @Value("${file.storage.local.path:uploads}")
    private String basePath;

    @Value("${file.storage.local.url-prefix:http://localhost:8080/api/uploads}")
    private String urlPrefix;

    @Override
    public String store(org.springframework.web.multipart.MultipartFile file, String path) throws IOException {
        Path fullPath = Paths.get(basePath, path);
        Files.createDirectories(fullPath.getParent());
        file.transferTo(fullPath.toFile());
        log.info("文件存储成功: {}", fullPath);
        return path;
    }

    @Override
    public void delete(String path) throws IOException {
        Path fullPath = Paths.get(basePath, path);
        Files.deleteIfExists(fullPath);
        log.info("文件删除成功: {}", fullPath);
    }

    @Override
    public String getAccessUrl(String path) {
        return urlPrefix + "/" + path;
    }

    @Override
    public String getStorageType() {
        return "LOCAL";
    }
}
```

### 6. 基础设施层 - OSS存储实现（示例）

```java
// OssStorageService.java
package com.example.ddd.infrastructure.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "OSS")
public class OssStorageService implements StorageService {

    @Autowired
    private OSS ossClient;

    @Value("${file.storage.oss.bucket-name}")
    private String bucketName;

    @Value("${file.storage.oss.endpoint}")
    private String endpoint;

    @Value("${file.storage.oss.url-prefix}")
    private String urlPrefix;

    @Override
    public String store(MultipartFile file, String path) throws IOException {
        ossClient.putObject(bucketName, path, file.getInputStream());
        log.info("OSS文件存储成功: {}", path);
        return path;
    }

    @Override
    public void delete(String path) throws IOException {
        ossClient.deleteObject(bucketName, path);
        log.info("OSS文件删除成功: {}", path);
    }

    @Override
    public String getAccessUrl(String path) {
        return urlPrefix + "/" + path;
    }

    @Override
    public String getStorageType() {
        return "OSS";
    }
}
```

### 7. 基础设施层 - 存储配置属性

```java
// StorageProperties.java
package com.example.ddd.infrastructure.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file.storage")
public class StorageProperties {

    /**
     * 存储类型：LOCAL/OSS/COS/S3
     */
    private String type = "LOCAL";

    /**
     * 允许的文件类型
     */
    private String allowedTypes = "jpg,jpeg,png,gif,webp";

    /**
     * 最大文件大小（字节）
     */
    private Long maxSize = 10485760L;

    /**
     * 待绑定文件过期时间（小时）
     */
    private Integer pendingExpireHours = 24;
}
```

### 8. 应用层 - 文件应用服务

```java
// FileApplicationService.java
package com.example.ddd.application.service;

import com.example.ddd.application.service.base.ApplicationService;
import com.example.ddd.domain.model.entity.FileInfo;
import com.example.ddd.domain.model.valueobject.BizType;
import com.example.ddd.domain.model.valueobject.FileStatus;
import com.example.ddd.domain.model.valueobject.StorageType;
import com.example.ddd.domain.repository.FileRepository;
import com.example.ddd.infrastructure.storage.StorageService;
import com.example.ddd.interfaces.rest.dto.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileApplicationService extends ApplicationService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private StorageService storageService;

    @Value("${file.storage.pending-expire-hours:24}")
    private Integer pendingExpireHours;

    /**
     * 上传文件（待绑定状态）
     */
    @Transactional
    public FileUploadResponse upload(MultipartFile file, Long uploaderId) {
        return upload(file, uploaderId, null);
    }

    /**
     * 上传文件并指定业务类型
     */
    @Transactional
    public FileUploadResponse upload(MultipartFile file, Long uploaderId, BizType bizType) {
        // 1. 生成文件信息
        String fileKey = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storagePath = datePath + "/" + fileKey + "." + extension;

        // 2. 存储文件
        try {
            storageService.store(file, storagePath);
        } catch (Exception e) {
            log.error("文件存储失败", e);
            throw new RuntimeException("文件存储失败: " + e.getMessage());
        }

        // 3. 构建访问URL
        String accessUrl = storageService.getAccessUrl(storagePath);

        // 4. 保存文件元数据
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileKey(fileKey);
        fileInfo.setFileName(originalFilename);
        fileInfo.setStoragePath(storagePath);
        fileInfo.setStorageType(StorageType.valueOf(storageService.getStorageType()));
        fileInfo.setFileSize(file.getSize());
        fileInfo.setContentType(file.getContentType());
        fileInfo.setAccessUrl(accessUrl);
        fileInfo.setBizType(bizType);
        fileInfo.setUploaderId(uploaderId);
        fileInfo.setStatus(FileStatus.PENDING);
        fileInfo.setExpireTime(LocalDateTime.now().plusHours(pendingExpireHours));

        fileRepository.save(fileInfo);

        log.info("文件上传成功: fileKey={}, path={}", fileKey, storagePath);

        return FileUploadResponse.builder()
                .fileKey(fileKey)
                .url(accessUrl)
                .fileName(originalFilename)
                .fileSize(file.getSize())
                .build();
    }

    /**
     * 绑定文件到业务
     */
    @Transactional
    public void bindToBusiness(String fileKey, BizType bizType, Long bizId) {
        FileInfo fileInfo = fileRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new RuntimeException("文件不存在: " + fileKey));

        fileInfo.bindBusiness(bizType, bizId);
        fileRepository.save(fileInfo);

        log.info("文件绑定成功: fileKey={}, bizType={}, bizId={}", fileKey, bizType, bizId);
    }

    /**
     * 批量绑定文件到业务
     */
    @Transactional
    public void bindToBusiness(List<String> fileKeys, BizType bizType, Long bizId) {
        for (String fileKey : fileKeys) {
            bindToBusiness(fileKey, bizType, bizId);
        }
    }

    /**
     * 获取业务关联的文件列表
     */
    public List<FileUploadResponse> getFilesByBusiness(BizType bizType, Long bizId) {
        return fileRepository.findByBizTypeAndBizId(bizType, bizId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取业务关联的主图（第一个文件）
     */
    public FileUploadResponse getMainImage(BizType bizType, Long bizId) {
        return fileRepository.findFirstByBizTypeAndBizId(bizType, bizId)
                .map(this::toResponse)
                .orElse(null);
    }

    /**
     * 删除业务关联的文件
     */
    @Transactional
    public void deleteByBusiness(BizType bizType, Long bizId) {
        List<FileInfo> files = fileRepository.findByBizTypeAndBizId(bizType, bizId);
        for (FileInfo file : files) {
            try {
                storageService.delete(file.getStoragePath());
            } catch (Exception e) {
                log.warn("物理文件删除失败: {}", file.getStoragePath(), e);
            }
            file.markDeleted();
            fileRepository.save(file);
        }
    }

    /**
     * 清理过期的待绑定文件
     */
    @Transactional
    public int cleanExpiredFiles() {
        List<FileInfo> expiredFiles = fileRepository.findExpiredPendingFiles();
        int count = 0;
        for (FileInfo file : expiredFiles) {
            try {
                storageService.delete(file.getStoragePath());
                file.markDeleted();
                fileRepository.save(file);
                count++;
            } catch (Exception e) {
                log.warn("清理过期文件失败: {}", file.getFileKey(), e);
            }
        }
        log.info("清理过期文件完成: {}个", count);
        return count;
    }

    private FileUploadResponse toResponse(FileInfo fileInfo) {
        return FileUploadResponse.builder()
                .fileKey(fileInfo.getFileKey())
                .url(fileInfo.getAccessUrl())
                .fileName(fileInfo.getFileName())
                .fileSize(fileInfo.getFileSize())
                .build();
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
```

### 9. 接口层 - 文件控制器

```java
// FileController.java
package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.FileApplicationService;
import com.example.ddd.interfaces.rest.dto.FileUploadRequest;
import com.example.ddd.interfaces.rest.dto.FileUploadResponse;
import com.example.ddd.interfaces.rest.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/files")
@Tag(name = "文件管理", description = "文件上传、绑定、查询接口")
public class FileController {

    @Autowired
    private FileApplicationService fileApplicationService;

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传单个文件，返回fileKey用于后续绑定业务")
    public Response<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizType", required = false) String bizType,
            @AuthenticationPrincipal Long userId) {

        log.info("文件上传: fileName={}, size={}, bizType={}",
                file.getOriginalFilename(), file.getSize(), bizType);

        FileUploadResponse response = fileApplicationService.upload(
                file,
                userId,
                bizType != null ? com.example.ddd.domain.model.valueobject.BizType.valueOf(bizType) : null
        );

        return Response.success(response);
    }

    /**
     * 上传多个文件
     */
    @PostMapping("/upload-multiple")
    @Operation(summary = "批量上传文件")
    public Response<List<FileUploadResponse>> uploadMultiple(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "bizType", required = false) String bizType,
            @AuthenticationPrincipal Long userId) {

        List<FileUploadResponse> results = new ArrayList<>();
        for (MultipartFile file : files) {
            FileUploadResponse response = fileApplicationService.upload(
                    file,
                    userId,
                    bizType != null ? com.example.ddd.domain.model.valueobject.BizType.valueOf(bizType) : null
            );
            results.add(response);
        }

        return Response.success(results);
    }

    /**
     * 绑定文件到业务
     */
    @PostMapping("/bind")
    @Operation(summary = "绑定文件到业务", description = "将已上传的文件绑定到具体业务")
    public Response<Void> bind(@RequestBody FileUploadRequest request) {
        fileApplicationService.bindToBusiness(
                request.getFileKey(),
                com.example.ddd.domain.model.valueobject.BizType.valueOf(request.getBizType()),
                request.getBizId()
        );
        return Response.success();
    }

    /**
     * 获取业务文件列表
     */
    @GetMapping("/business")
    @Operation(summary = "获取业务关联的文件")
    public Response<List<FileUploadResponse>> getBusinessFiles(
            @RequestParam String bizType,
            @RequestParam Long bizId) {

        List<FileUploadResponse> files = fileApplicationService.getFilesByBusiness(
                com.example.ddd.domain.model.valueobject.BizType.valueOf(bizType),
                bizId
        );

        return Response.success(files);
    }
}
```

### 10. DTO定义

```java
// FileUploadRequest.java
package com.example.ddd.interfaces.rest.dto;

import lombok.Data;

@Data
public class FileUploadRequest {
    private String fileKey;
    private String bizType;
    private Long bizId;
}

// FileUploadResponse.java
package com.example.ddd.interfaces.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {
    /**
     * 文件唯一标识（用于绑定业务）
     */
    private String fileKey;

    /**
     * 文件访问URL
     */
    private String url;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;
}
```

---

## 四、配置文件

### application.yml 新增

```yaml
# 文件存储配置
file:
  storage:
    # 存储类型：LOCAL/OSS/COS/S3
    type: ${FILE_STORAGE_TYPE:LOCAL}
    # 允许的文件类型
    allowed-types: ${FILE_ALLOWED_TYPES:jpg,jpeg,png,gif,webp}
    # 最大文件大小（10MB）
    max-size: ${FILE_MAX_SIZE:10485760}
    # 待绑定文件过期时间（小时）
    pending-expire-hours: ${FILE_PENDING_EXPIRE:24}

    # 本地存储配置
    local:
      path: ${FILE_LOCAL_PATH:uploads}
      url-prefix: ${FILE_LOCAL_URL_PREFIX:http://localhost:8080/api/uploads}

    # 阿里云OSS配置（当type=OSS时生效）
    oss:
      endpoint: ${OSS_ENDPOINT:}
      access-key-id: ${OSS_ACCESS_KEY_ID:}
      access-key-secret: ${OSS_ACCESS_KEY_SECRET:}
      bucket-name: ${OSS_BUCKET_NAME:}
      url-prefix: ${OSS_URL_PREFIX:}

# Spring文件上传配置
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB
```

---

## 五、使用方式

### 1. 前端上传流程

```
1. 上传文件 → POST /files/upload
   响应: { fileKey: "xxx", url: "http://..." }

2. 保存 fileKey 到表单隐藏字段

3. 提交业务表单时，后端接收 fileKey 并绑定：
   fileApplicationService.bindToBusiness(fileKey, BizType.PRODUCT_IMAGE, productId);
```

### 2. 商品服务集成示例

```java
// 在 ProductApplicationService 中
@Transactional
public void createProduct(ProductCreateRequest request, String mainImageFileKey) {
    // 1. 创建商品
    Product product = new Product();
    // ... 设置商品属性
    productRepository.save(product);

    // 2. 绑定图片文件
    if (mainImageFileKey != null) {
        fileApplicationService.bindToBusiness(
            mainImageFileKey,
            BizType.PRODUCT_IMAGE,
            product.getId()
        );
    }
}

// 查询商品时获取图片
public ProductDetailResponse getProductDetail(Long productId) {
    Product product = productRepository.findById(productId);
    FileUploadResponse mainImage = fileApplicationService.getMainImage(
        BizType.PRODUCT_IMAGE,
        productId
    );
    // ... 组装响应
}
```

---

## 六、定时清理任务

```java
// TempFileCleanJob.java
package com.example.ddd.interfaces.job;

import com.example.ddd.application.service.FileApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TempFileCleanJob {

    @Autowired
    private FileApplicationService fileApplicationService;

    /**
     * 每小时清理一次过期的待绑定文件
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredFiles() {
        log.info("开始清理过期文件...");
        int count = fileApplicationService.cleanExpiredFiles();
        log.info("清理过期文件完成: {}个", count);
    }
}
```

---

## 七、方案优势

| 特性 | 说明 |
|-----|------|
| **存储扩展性** | 通过 StorageService 接口，可轻松切换本地/OSS/COS/S3 |
| **业务解耦** | 业务表不需要存储图片字段，通过 biz_type + biz_id 关联 |
| **文件管理** | 独立文件表管理所有文件元数据，支持统计、清理 |
| **安全控制** | 待绑定文件24小时后自动清理，避免垃圾文件堆积 |
| **去重支持** | file_hash 字段支持文件去重 |
| **审计追踪** | uploader_id 记录上传者，便于追溯 |

---

## 八、前端配合修改

前端上传后需要保存 `fileKey`，提交商品时传递给后端：

```typescript
// 上传成功后保存 fileKey
onChange: ({ file }) => {
  if (file.status === 'done' && file.response?.code === 200) {
    setFileKey(file.response.data.fileKey);  // 保存 fileKey
    setImageUrl(file.response.data.url);     // 用于预览
  }
}

// 提交时传递 fileKey
const handleSubmit = async (values) => {
  const submitData = {
    ...values,
    mainImageFileKey: fileKey,  // 传递 fileKey，不是 URL
  };
  await request.post('/products', submitData);
};
```
