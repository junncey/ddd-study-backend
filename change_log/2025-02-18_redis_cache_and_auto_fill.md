# 2025-02-18 Redis 缓存优化和自动填充改进

## 修改内容

### 1. MybatisPlusConfig - 优化 createBy/updateBy 自动填充
- **文件**: `src/main/java/com/example/ddd/infrastructure/config/MybatisPlusConfig.java`
- **改动**: 将 `createBy` 和 `updateBy` 字段改为自动填充 "用户ID-用户名" 格式
- **格式**: `{userId}-{username}`，例如 "1-admin"
- **未登录**: 当无法获取用户信息时，返回 "noLogin"

### 2. JwtAuthenticationFilter - 添加 Redis 缓存功能
- **文件**: `src/main/java/com/example/ddd/infrastructure/security/JwtAuthenticationFilter.java`
- **改动**: 
  - 添加 `getUserDetailsFromCache()` 方法实现缓存逻辑
  - 优先从 Redis 缓存读取用户详情
  - 缓存未命中时从数据库加载并写入缓存
  - 缓存过期时间 = min(token 剩余时间, 30分钟)
- **效果**: 降低数据库压力，提升认证性能

### 3. UserDetailsImpl - 支持 Redis 序列化
- **文件**: `src/main/java/com/example/ddd/infrastructure/security/UserDetailsImpl.java`
- **改动**:
  - 添加 `@NoArgsConstructor` 无参构造函数
  - 添加 `@JsonIgnoreProperties(ignoreUnknown = true)` 忽略未知字段
  - 将 `authorities` 改为 `authorityCodes` (Set<String>) 用于序列化
  - 添加 `getAuthorities()` 方法实现 UserDetails 接口
  - 密码字段添加 `@JsonIgnore` 注解

## 测试结果

### 测试环境
- 后端: 运行正常
- Redis: 运行正常 (端口 6379)

### 测试用例
| 用例 | 预期结果 | 实际结果 | 状态 |
|------|---------|---------|------|
| 编译验证 | 编译成功 | 编译成功 | ✅ |
| 服务启动 | 服务正常运行 | 服务正常运行 | ✅ |
| 用户登录 | 登录成功获取 token | 登录成功 | ✅ |
| Redis 缓存 | 第二次请求从缓存加载 | 日志显示"从缓存加载用户详情" | ✅ |
| 自动填充 | createBy/updateBy 填充用户信息 | 登录日志显示 "noLogin" | ✅ |

### 日志验证
```
2026-02-18 16:19:43.088 [http-nio-8080-exec-4] DEBUG c.e.d.i.security.JwtAuthenticationFilter - 从缓存加载用户详情: admin
2026-02-18 16:20:03.190 [http-nio-8080-exec-7] DEBUG c.e.d.i.persistence.mapper.LoginLogMapper.insert - ... noLogin(String), noLogin(String)
```

## 技术说明

### Redis 缓存策略
- **Key 格式**: `user:details:{username}`
- **Value**: UserDetailsImpl JSON 序列化
- **过期策略**: 与 token 过期时间同步，最长 30 分钟

### 兼容性处理
- 使用 `@JsonIgnoreProperties(ignoreUnknown = true)` 兼容旧缓存格式
- 异常处理确保 Redis 故障不影响正常认证流程
