package com.example.ddd.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传请求
 *
 * @author DDD Demo
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadRequest {

    @Schema(description = "文件唯一标识", example = "abc123def456")
    private String fileKey;

    @Schema(description = "业务类型", example = "PRODUCT_IMAGE")
    private String bizType;

    @Schema(description = "业务ID", example = "1")
    private Long bizId;
}
