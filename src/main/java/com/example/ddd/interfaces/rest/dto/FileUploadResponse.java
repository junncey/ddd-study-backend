package com.example.ddd.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 文件上传响应
 *
 * @author DDD Demo
 */
@Data
@Builder
@Schema(description = "文件上传响应")
public class FileUploadResponse {

    @Schema(description = "文件唯一标识（用于后续绑定业务）", example = "abc123def456")
    private String fileKey;

    @Schema(description = "文件访问URL", example = "http://localhost:8080/api/uploads/2026/02/19/abc123.jpg")
    private String url;

    @Schema(description = "原始文件名", example = "product.jpg")
    private String fileName;

    @Schema(description = "文件大小（字节）", example = "102400")
    private Long fileSize;
}
