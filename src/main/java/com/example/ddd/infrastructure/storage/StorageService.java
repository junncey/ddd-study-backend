package com.example.ddd.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 * 支持本地存储和云存储的统一抽象
 *
 * @author DDD Demo
 */
public interface StorageService {

    /**
     * 存储文件
     *
     * @param file 上传的文件
     * @param path 相对存储路径（不含文件名）
     * @param filename 存储文件名
     * @return 实际存储的完整相对路径
     * @throws Exception 存储异常
     */
    String store(MultipartFile file, String path, String filename) throws Exception;

    /**
     * 删除文件
     *
     * @param path 文件相对路径
     * @throws Exception 删除异常
     */
    void delete(String path) throws Exception;

    /**
     * 获取文件访问URL
     *
     * @param path 文件相对路径
     * @return 完整访问URL
     */
    String getAccessUrl(String path);

    /**
     * 获取存储类型
     *
     * @return 存储类型标识
     */
    String getStorageType();

    /**
     * 检查文件是否存在
     *
     * @param path 文件相对路径
     * @return 是否存在
     */
    boolean exists(String path);
}
