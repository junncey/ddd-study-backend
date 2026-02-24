package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.FileApplicationService;
import com.example.ddd.domain.model.valueobject.BizType;
import com.example.ddd.interfaces.rest.dto.FileUploadRequest;
import com.example.ddd.interfaces.rest.dto.FileUploadResponse;
import com.example.ddd.interfaces.rest.vo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件管理控制器
 * 提供文件上传、绑定、查询等接口
 *
 * @author DDD Demo
 */
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件上传、绑定、查询接口")
public class FileController {

    private final FileApplicationService fileApplicationService;

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传单个文件，返回fileKey用于后续绑定业务")
    public Response<FileUploadResponse> upload(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务类型（可选）") @RequestParam(value = "bizType", required = false) String bizType,
            @AuthenticationPrincipal Long userId) {

        log.info("文件上传: fileName={}, size={}, bizType={}",
                file.getOriginalFilename(), file.getSize(), bizType);

        BizType bizTypeEnum = bizType != null ? BizType.fromName(bizType) : null;
        FileUploadResponse response = fileApplicationService.upload(file, userId, bizTypeEnum);

        return Response.success(response);
    }

    /**
     * 上传多个文件
     */
    @PostMapping("/upload-multiple")
    @Operation(summary = "批量上传文件", description = "上传多个文件，返回fileKey列表")
    public Response<List<FileUploadResponse>> uploadMultiple(
            @Parameter(description = "上传的文件列表") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "业务类型（可选）") @RequestParam(value = "bizType", required = false) String bizType,
            @AuthenticationPrincipal Long userId) {

        log.info("批量文件上传: count={}, bizType={}", files.length, bizType);

        BizType bizTypeEnum = bizType != null ? BizType.fromName(bizType) : null;
        List<FileUploadResponse> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                FileUploadResponse response = fileApplicationService.upload(file, userId, bizTypeEnum);
                results.add(response);
            } catch (Exception e) {
                log.error("文件上传失败: {}", file.getOriginalFilename(), e);
            }
        }

        return Response.success(results);
    }

    /**
     * 上传商品图片（支持多图片上传）
     */
    @PostMapping("/product-images")
    @Operation(summary = "上传商品图片", description = "上传商品图片，支持多图片上传，返回fileKey列表用于商品创建/修改时绑定")
    public Response<List<FileUploadResponse>> uploadProductImages(
            @Parameter(description = "上传的图片文件列表") @RequestParam("files") MultipartFile[] files,
            @AuthenticationPrincipal Long userId) {

        log.info("商品图片上传: count={}", files.length);

        List<FileUploadResponse> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // 商品图片指定为 PRODUCT_IMAGE 类型
                FileUploadResponse response = fileApplicationService.upload(file, userId, BizType.PRODUCT_IMAGE);
                results.add(response);
            } catch (Exception e) {
                log.error("商品图片上传失败: {}", file.getOriginalFilename(), e);
            }
        }

        return Response.success(results);
    }

    /**
     * 绑定文件到业务
     */
    @PostMapping("/bind")
    @Operation(summary = "绑定文件到业务", description = "将已上传的文件绑定到具体业务")
    public Response<Void> bind(@RequestBody FileUploadRequest request) {
        log.info("文件绑定: fileKey={}, bizType={}, bizId={}",
                request.getFileKey(), request.getBizType(), request.getBizId());

        fileApplicationService.bindToBusiness(
                request.getFileKey(),
                BizType.fromName(request.getBizType()),
                request.getBizId()
        );

        return Response.success();
    }

    /**
     * 批量绑定文件到业务
     */
    @PostMapping("/bind-batch")
    @Operation(summary = "批量绑定文件", description = "批量将文件绑定到业务")
    public Response<Void> bindBatch(@RequestBody List<FileUploadRequest> requests) {
        log.info("批量文件绑定: count={}", requests.size());

        for (FileUploadRequest request : requests) {
            fileApplicationService.bindToBusiness(
                    request.getFileKey(),
                    BizType.fromName(request.getBizType()),
                    request.getBizId()
            );
        }

        return Response.success();
    }

    /**
     * 获取业务关联的文件列表
     */
    @GetMapping("/business")
    @Operation(summary = "获取业务文件", description = "获取指定业务关联的所有文件")
    public Response<List<FileUploadResponse>> getBusinessFiles(
            @Parameter(description = "业务类型") @RequestParam String bizType,
            @Parameter(description = "业务ID") @RequestParam Long bizId) {

        List<FileUploadResponse> files = fileApplicationService.getFilesByBusiness(
                BizType.fromName(bizType),
                bizId
        );

        return Response.success(files);
    }

    /**
     * 获取业务主图
     */
    @GetMapping("/main-image")
    @Operation(summary = "获取业务主图", description = "获取指定业务关联的第一个文件（主图）")
    public Response<FileUploadResponse> getMainImage(
            @Parameter(description = "业务类型") @RequestParam String bizType,
            @Parameter(description = "业务ID") @RequestParam Long bizId) {

        FileUploadResponse file = fileApplicationService.getMainImage(
                BizType.fromName(bizType),
                bizId
        );

        return Response.success(file);
    }

    /**
     * 根据fileKey获取文件信息
     */
    @GetMapping("/{fileKey}")
    @Operation(summary = "获取文件信息", description = "根据fileKey获取文件详情")
    public Response<FileUploadResponse> getByFileKey(
            @Parameter(description = "文件唯一标识") @PathVariable String fileKey) {

        FileUploadResponse file = fileApplicationService.getByFileKey(fileKey);
        if (file == null) {
            return Response.fail("文件不存在");
        }

        return Response.success(file);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileKey}")
    @Operation(summary = "删除文件", description = "根据fileKey删除文件")
    public Response<Void> deleteFile(
            @Parameter(description = "文件唯一标识") @PathVariable String fileKey) {

        log.info("文件删除: fileKey={}", fileKey);

        fileApplicationService.deleteFile(fileKey);

        return Response.success();
    }
}
