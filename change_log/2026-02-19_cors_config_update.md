# CORS 配置更新 - 允许所有 IP 访问

## 日期
2026-02-19

## 变更内容

### 后端修改

#### 1. `application-dev.yml`
完整重写开发环境配置：
- 添加 `server.address: 0.0.0.0` - 绑定所有网络接口
- `CORS_ALLOWED_ORIGINS: "*"` - 允许所有来源
- `CSP_CONNECT_SRC: "*"` - 允许所有 CSP 连接
- `SECURITY_HEADERS_STRICT: false` - 放宽安全头限制
- 启用 Swagger 和 Druid 监控

#### 2. `SecurityConfig.java`
- 更新 `@Value` 默认值为 `"*"`，允许所有来源
- 优化 CORS 配置逻辑，显式处理 `*` 通配符

#### 3. `SecurityHeadersConfig.java`
- 添加 `CSP_CONNECT_SRC` 配置项
- 添加 `SECURITY_HEADERS_STRICT` 配置项
- Content-Security-Policy 的 `connect-src` 根据环境动态配置

### 前端修改

#### 4. `frontend/src/utils/request.ts`
**关键修复**：原代码硬编码 `localhost:8080`，导致其他设备访问时 API 请求失败
- 添加 `getBaseURL()` 函数动态获取 API 地址
- 支持环境变量 `VITE_API_BASE_URL` 自定义配置
- 默认使用 `window.location.hostname + :8080` 动态构建

#### 5. `frontend/vite.config.ts`
- 添加 `server.host: '0.0.0.0'` 允许外部访问前端服务

## 问题根因
1. 后端 CORS 默认只允许 localhost
2. 后端 CSP 的 `connect-src 'self'` 限制同源连接
3. **前端硬编码 `localhost:8080`**，其他设备访问时请求发往本地

## 修改文件
1. `src/main/resources/application-dev.yml`
2. `src/main/java/com/example/ddd/infrastructure/config/SecurityConfig.java`
3. `src/main/java/com/example/ddd/infrastructure/config/SecurityHeadersConfig.java`
4. `frontend/src/utils/request.ts`
5. `frontend/vite.config.ts`

## 重启步骤
```bash
# 后端
cd D:/ddd-study2
mvn spring-boot:run

# 前端
cd D:/ddd-study2/frontend
npm run dev
```

## 测试验证
- [x] 后端编译通过
- [x] 前端编译通过
- [x] 前端 TypeScript 警告已修复
- [ ] 功能测试（需重启前后端服务后验证）

## 前端 TypeScript 警告修复
移除未使用的导入：
1. `src/components/ProductReviews/index.tsx` - 移除 `Button`、`EditOutlined`
2. `src/pages/consumer/ProductDetail/index.tsx` - 移除 `Space`

## 注意事项
- 此配置仅适用于开发环境
- 生产环境应配置具体的允许域名
- 其他设备访问时使用服务器实际 IP 地址
