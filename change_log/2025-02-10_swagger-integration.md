# Swagger 接口文档集成开发日志

## 日期
2025-02-10

## 变更说明
在项目中集成 SpringDoc OpenAPI（Swagger），提供在线 API 接口文档和调试功能。

## 变更内容

### 1. 依赖配置
- 在 `pom.xml` 中添加 SpringDoc OpenAPI 依赖：
  - `springdoc-openapi-starter-webmvc-ui:2.3.0`
  - 在 `properties` 中添加 `springdoc.version` 版本管理

### 2. OpenAPI 配置类

#### 新增文件：`infrastructure/config/OpenApiConfig.java`
- 配置 OpenAPI 文档基本信息（标题、描述、版本、许可证等）
- 配置 JWT Bearer 认证方式
- 添加全局参数（租户ID、链路追踪ID等预留字段）

### 3. 配置文件更新

#### application.yml 新增配置
```yaml
# SpringDoc OpenAPI (Swagger) 配置
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    default-models-expand-depth: 2
    syntax-highlight:
      enabled: true
    display-request-duration: true
    operations-sorter: method
    tags-sorter: alpha
  group-configs:
    - group: default
      paths-to-match: /**
      packages-to-scan: com.example.ddd.interfaces.rest.controller
  enable: true
  allow-url-with-spaces: true
```

### 4. 安全配置更新

#### `infrastructure/config/SecurityConfig.java` 更新
- 完善 Swagger 端点的公开访问配置
- 添加以下端点到 `PUBLIC_ENDPOINTS`：
  - `/swagger-ui.html`
  - `/swagger-ui/**`
  - `/v3/api-docs/**`
  - `/swagger-resources/**`
  - `/webjars/**`
  - `/api-docs/**`

### 5. 文档更新

#### README.md 更新
- 技术栈中新增：SpringDoc OpenAPI、Spring Security、JWT
- 新增"API 文档"章节，包含：
  - Swagger UI 访问地址
  - JWT 认证使用说明
  - OpenAPI JSON 规范访问地址

## 访问地址

### Swagger UI
- http://localhost:8080/api/swagger-ui.html
- http://localhost:8080/api/swagger-ui/index.html

### OpenAPI JSON
- http://localhost:8080/api/v3/api-docs

## 使用说明

### JWT 认证配置
1. 调用 `/api/auth/login` 接口获取 Token
2. 点击 Swagger UI 右上角的 **Authorize** 按钮
3. 输入 Token（格式：`Bearer your-token-here`）
4. 确认后，所有请求会自动携带认证信息

### 功能特性
- 在线查看所有 API 接口
- 在线调试接口（发送请求、查看响应）
- 支持 JWT Bearer 认证
- 接口按标签和字母排序
- 显示请求时长
- 支持 JSON 语法高亮
- 可下载 OpenAPI JSON 规范

## 后续优化建议

1. **接口分组**: 按业务模块对接口进行分组展示
2. **注解完善**: 为所有接口添加详细的 `@Operation` 注解
3. **示例数据**: 添加请求/响应示例数据
4. **API 版本管理**: 实现多版本 API 文档
5. **离线文档**: 支持导出离线文档（PDF/HTML）
6. **接口变更记录**: 记录接口变更历史
7. **环境配置**: 支持多环境配置（开发/测试/生产）

## 注意事项

1. 生产环境建议关闭 Swagger 或添加访问权限控制
2. 敏感接口（如删除、修改）需要添加详细的安全说明
3. 定期更新文档，保持与实际代码一致
4. 使用语义化的接口标签，便于文档组织
