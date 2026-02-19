# 商品图片上传功能实现

## 日期
2026-02-19

## 问题描述
商品图片上传修改后，商品页图片不显示。

### 根本原因分析

| 问题 | 描述 |
|------|------|
| **后端没有图片上传接口** | 没有 `/files/upload` 端点，无法接收文件 |
| **前端 Upload 组件未配置** | `ProductManage/index.tsx` 的 `<Upload>` 组件没有 `action` 属性 |
| **表单不包含 mainImage** | `handleSubmit` 函数只发送表单字段，没有图片URL |
| **数据库 main_image 为空** | 没有存储图片URL |
| **没有静态资源服务** | 没有配置 WebMvcConfigurer 来提供图片访问 |

---

## 前端修改（已完成）

### 修改文件
`frontend/src/pages/shop-admin/ProductManage/index.tsx`

### 修改内容

1. **添加类型定义**
   - Product 接口添加 `mainImage?: string` 字段
   - 添加 `UploadProps` 和 `RcFile` 类型导入

2. **新增状态**
   ```typescript
   const [uploadedImageUrl, setUploadedImageUrl] = useState<string>('');
   const [previewOpen, setPreviewOpen] = useState(false);
   const [previewImage, setPreviewImage] = useState('');
   ```

3. **新增工具函数**
   - `getUploadUrl()` - 获取上传接口URL
   - `beforeUpload()` - 上传前校验（图片类型、大小限制）
   - `handlePreview()` - 图片预览处理
   - `getBase64()` - Base64转换

4. **配置 Upload 组件**
   ```typescript
   const uploadProps: UploadProps = {
     name: 'file',
     action: getUploadUrl(),
     listType: 'picture-card',
     fileList,
     beforeUpload,
     onPreview: handlePreview,
     onChange: ({ fileList, file }) => {
       // 处理上传成功/失败
       if (file.status === 'done' && file.response?.code === 200) {
         setUploadedImageUrl(file.response.data.url);
       }
     },
     headers: {
       Authorization: `Bearer ${localStorage.getItem('token')}`,
     },
   };
   ```

5. **修改 handleEdit 函数**
   - 编辑商品时加载已有图片到 fileList
   - 设置 uploadedImageUrl 状态

6. **修改 handleSubmit 函数**
   - 提交数据时包含 `mainImage` 字段

7. **添加图片预览弹窗**

---

## 后端设计方案（待实现）

### 1. 文件目录结构

```
src/main/java/com/example/ddd/
├── interfaces/rest/controller/
│   └── FileController.java          # 新建：文件上传接口
├── infrastructure/config/
│   └── WebConfig.java               # 新建：静态资源配置
└── interfaces/rest/dto/
    └── FileUploadResponse.java      # 新建：上传响应DTO

uploads/                              # 新建：图片存储目录（项目根目录）
```

### 2. 配置文件 application.yml 新增

```yaml
# 文件上传配置
file:
  upload:
    # 存储路径（相对于项目根目录）
    path: ${FILE_UPLOAD_PATH:uploads}
    # 访问URL前缀
    url-prefix: /uploads
    # 允许的文件类型
    allowed-types: jpg,jpeg,png,gif,webp
    # 最大文件大小（10MB）
    max-size: 10485760
```

### 3. FileController.java

```java
package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.interfaces.rest.dto.FileUploadResponse;
import com.example.ddd.interfaces.rest.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/uploads}")
    private String urlPrefix;

    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,webp}")
    private String allowedTypes;

    @Value("${file.upload.max-size:10485760}")
    private long maxSize;

    /**
     * 上传单个文件
     * POST /files/upload
     */
    @PostMapping("/upload")
    public Response<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        // 1. 校验文件
        validateFile(file);

        // 2. 生成存储路径：uploads/年/月/日/UUID.ext
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        Path directory = Paths.get(uploadPath, datePath);
        Path filePath = directory.resolve(newFilename);

        try {
            // 3. 创建目录并保存文件
            Files.createDirectories(directory);
            file.transferTo(filePath.toFile());

            // 4. 构建响应
            String url = urlPrefix + "/" + datePath + "/" + newFilename;
            FileUploadResponse response = FileUploadResponse.builder()
                    .url("/api" + url)
                    .fileName(newFilename)
                    .originalName(originalFilename)
                    .fileSize(file.getSize())
                    .build();

            log.info("文件上传成功: {}", url);
            return Response.success(response);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Response.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传多个文件
     * POST /files/upload-multiple
     */
    @PostMapping("/upload-multiple")
    public Response<List<FileUploadResponse>> uploadMultiple(@RequestParam("files") MultipartFile[] files) {
        List<FileUploadResponse> results = new ArrayList<>();
        for (MultipartFile file : files) {
            Response<FileUploadResponse> result = upload(file);
            if (result.getCode() == 200) {
                results.add(result.getData());
            }
        }
        return Response.success(results);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小超过限制（最大10MB）");
        }
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!allowedTypes.toLowerCase().contains(extension)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
```

### 4. FileUploadResponse.java

```java
package com.example.ddd.interfaces.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {
    /**
     * 图片访问URL
     */
    private String url;

    /**
     * 服务器文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;
}
```

### 5. WebConfig.java

```java
package com.example.ddd.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    /**
     * 配置静态资源映射
     * 访问 /uploads/** 映射到本地 uploads/ 目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
```

### 6. SecurityConfig.java 修改

在 `filterChain` 方法的 `requestMatchers` 中添加：

```java
.requestMatchers("/uploads/**").permitAll()
.requestMatchers("/files/**").authenticated()  // 上传接口需要认证
```

### 7. 文件存储策略

| 项目 | 策略 |
|-----|------|
| **目录结构** | `uploads/{年}/{月}/{日}/{UUID}.{扩展名}` |
| **文件命名** | UUID + 原扩展名 |
| **返回URL** | `/api/uploads/{年}/{月}/{日}/{UUID}.{扩展名}` |
| **最大大小** | 10MB |
| **支持格式** | jpg, jpeg, png, gif, webp |

---

## 测试计划

### 后端测试

1. **启动后端服务**
   ```bash
   mvn spring-boot:run
   ```

2. **测试上传接口**
   ```bash
   # 登录获取token
   # 上传图片
   curl -X POST http://localhost:8080/api/files/upload \
     -H "Authorization: Bearer <token>" \
     -F "file=@test.jpg"
   ```

3. **验证图片访问**
   - 直接浏览器访问返回的URL
   - 确认图片能正常显示

### 前端测试

1. **启动前端服务**
   ```bash
   cd frontend && npm run dev
   ```

2. **测试流程**
   - 登录管理员账号
   - 进入商品管理页面
   - 创建/编辑商品，上传图片
   - 保存后查看商品列表和详情页
   - 确认图片正常显示

---

## 注意事项

1. **生产环境安全**
   - 配置 `FILE_UPLOAD_PATH` 环境变量指定存储路径
   - 定期清理无用图片
   - 考虑使用OSS/CDN存储图片

2. **跨域访问**
   - 图片URL以 `/api/uploads/` 开头，自动走CORS配置

3. **文件大小限制**
   - Spring Boot默认限制1MB，需要在配置中调整：
   ```yaml
   spring:
     servlet:
       multipart:
         max-file-size: 10MB
         max-request-size: 50MB
   ```
