# 单点登录（SSO）机制实现 - 变更日志

## 日期
2026-02-18

## 需求描述
- 实现单点登录：同一用户只能在一个地方登录
- 新登录自动使旧 token 失效
- 删除复杂的黑名单机制，改用更简洁的白名单模式

## 技术方案

### 核心思路
使用 Redis 存储**用户当前有效的 token**（白名单模式），而不是存储黑名单。

```
Redis Key: user:token:{username}
Redis Value: 当前有效的 access token
```

### 工作流程
1. **登录时**：将新 token 存入 Redis，覆盖旧 token
2. **验证时**：检查请求的 token 是否与 Redis 中存储的一致
3. **刷新时**：更新 Redis 中的 token
4. **登出时**：删除 Redis 中的 token

### 优点
- 天然实现单点登录（新登录自动踢掉旧登录）
- 无需维护黑名单
- 代码更简洁

## 文件变更

### 删除文件
- `src/main/java/com/example/ddd/infrastructure/security/TokenBlacklistService.java` - 删除黑名单服务

### 新增文件
- `src/main/java/com/example/ddd/infrastructure/security/UserTokenService.java` - 用户 token 管理服务

### 修改文件

1. **`JwtAuthenticationFilter.java`**
   - 将黑名单检查改为 token 一致性检查
   - 如果 token 与 Redis 中不一致，说明用户在其他地方登录，拒绝认证

2. **`AuthApplicationService.java`**
   - 登录时存储 token 到 Redis
   - 刷新 token 时更新 Redis
   - 登出时删除 Redis 中的 token

3. **`AuthController.java`**
   - 简化 logout 方法，不再需要传递 token

## Redis 存储

| Key 格式 | 说明 | 过期时间 |
|---------|------|---------|
| `user:token:{username}` | 用户当前有效的 token | Token 剩余有效期 |

## 代码示例

### UserTokenService 核心方法

```java
// 存储用户 token
public void storeUserToken(String username, String token);

// 获取用户当前 token
public String getUserToken(String username);

// 删除用户 token（登出）
public void removeUserToken(String username);

// 验证 token 是否是当前有效的
public boolean isCurrentValidToken(String username, String token);
```

### JwtAuthenticationFilter 验证逻辑

```java
// 检查 token 是否是用户当前有效的 token（单点登录）
if (!userTokenService.isCurrentValidToken(username, jwt)) {
    log.debug("Token 已失效（用户在其他地方登录）");
    // 拒绝认证
}
```

## 测试验证

### 测试环境
- 后端：运行正常（端口 8080）
- 前端：运行正常（端口 5173）
- Redis：运行正常（单点登录功能可用）
- 测试工具：agent-browser MCP + curl

### 测试用例

| 用例 | 预期结果 | 实际结果 | 状态 |
|------|---------|---------|------|
| 用户登录（设备1） | 成功，获取 Token 1 | 成功 | ✅ |
| 用户再次登录（设备2） | 成功，获取 Token 2 | 成功 | ✅ |
| 用 Token 1（旧）访问 API | 返回 401 | 返回 401 "请先登录" | ✅ |
| 用 Token 2（新）访问 API | 返回 200 | 返回 200，正常数据 | ✅ |
| 退出登录 | 清除本地存储，跳转登录页 | 成功，localStorage 清空 | ✅ |

### 测试结论
所有测试用例通过，单点登录功能正常工作。

### 测试过程记录

**1. 设备1登录**
```
Token 1: eyJhbGciOiJIUzUxMiJ9...（登录成功）
```

**2. 设备2登录（模拟 curl 请求）**
```
Token 2: eyJhbGciOiJIUzUxMiJ9...（登录成功，覆盖 Token 1）
```

**3. 验证旧 Token 失效**
```bash
# Token 1 访问
curl -H "Authorization: Bearer $TOKEN1" /api/auth/current
→ {"code":401,"message":"请先登录"}  # 已失效

# Token 2 访问
curl -H "Authorization: Bearer $TOKEN2" /api/auth/current
→ {"code":200,"data":{...}}  # 正常
```

## 注意事项

1. **Redis 依赖**：单点登录功能需要 Redis 支持
   - 如果 Redis 未运行，系统仍可正常工作（兼容模式）
   - 但单点登录功能会失效（允许所有有效 token）
   - 建议在生产环境中确保 Redis 高可用

2. **前端无需修改**：前端已经实现了调用 `/auth/logout` 接口

## 附加修复

### 修复 /auth/current 接口未认证时返回 500 的问题

**问题**：未认证用户访问 `/auth/current` 接口时，`@AuthenticationPrincipal` 参数为 null，导致 NullPointerException。

**修复**：在 `AuthController.getCurrentUser()` 方法中添加 null 检查：
```java
if (userDetails == null) {
    return Response.fail(401, "请先登录");
}
```
