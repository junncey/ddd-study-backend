package com.example.ddd.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地存储服务实现
 * 将文件存储在本地文件系统
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "LOCAL", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Value("${file.storage.local.path:uploads}")
    private String basePath;

    @Value("${file.storage.local.url-prefix:http://localhost:8080/api/uploads}")
    private String urlPrefix;

    @Override
    public String store(org.springframework.web.multipart.MultipartFile file, String path, String filename) throws IOException {
        // 构建完整存储路径（使用绝对路径）
        Path basePathAbs = Paths.get(basePath).toAbsolutePath();
        Path fullPath = basePathAbs.resolve(path).resolve(filename);

        // 创建目录
        Files.createDirectories(fullPath.getParent());

        // 保存文件（使用绝对路径）
        file.transferTo(fullPath);

        // 返回相对路径
        String relativePath = path + "/" + filename;
        log.info("文件存储成功: {} -> {}", relativePath, fullPath);

        return relativePath;
    }

    @Override
    public void delete(String path) throws IOException {
        Path fullPath = Paths.get(basePath, path);
        boolean deleted = Files.deleteIfExists(fullPath);
        if (deleted) {
            log.info("文件删除成功: {}", path);
        } else {
            log.warn("文件不存在，删除失败: {}", path);
        }
    }

    @Override
    public String getAccessUrl(String path) {
        return urlPrefix + "/" + path;
    }

    @Override
    public String getStorageType() {
        return "LOCAL";
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(Paths.get(basePath, path));
    }
}
