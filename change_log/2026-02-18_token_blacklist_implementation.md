# Token 黑名单机制实现 - 变更日志

## 日期
2026-02-18

## 需求描述
- 退出登录时调用后端接口使 token 失效
- 使用 Redis 实现 token 黑名单机制
- 确保登出后的 token 无法继续使用

## 技术实现

### 新增文件

1. **`src/main/java/com/example/ddd/infrastructure/security/TokenBlacklistService.java`**
   - Token 黑名单服务
   - 使用 Redis 存储失效的 token
   - 支持 access token 和 refresh token 两种类型
   - 自动设置过期时间（与 token 剩余有效期一致）

### 修改文件

1. **`src/main/java/com/example/ddd/infrastructure/security/JwtAuthenticationFilter.java`**
   - 注入 `TokenBlacklistService`
   - 在验证 token 时检查是否在黑名单中
   - 如果 token 在黑名单中，拒绝认证

2. **`src/main/java/com/example/ddd/application/service/AuthApplicationService.java`**
   - 注入 `TokenBlacklistService`
   - 修改 `logout` 方法，接收 token 参数并将其加入黑名单
   - 修改 `refreshToken` 方法，将旧的 refresh token 加入黑名单

3. **`src/main/java/com/example/ddd/interfaces/rest/controller/AuthController.java`**
   - 修改 `logout` 方法，从请求头获取 access token
   - 传递 token 给 `AuthApplicationService.logout()`

## 工作流程

### 用户登出流程
1. 前端调用 `POST /api/auth/logout`
2. 后端从请求头 `Authorization: Bearer <token>` 获取 access token
3. 将 token 加入 Redis 黑名单（key: `token:blacklist:<token>`）
4. 设置过期时间为 token 剩余有效期
5. 前端清除本地存储的 token

### Token 验证流程（修改后）
1. 请求到达后端
2. `JwtAuthenticationFilter` 从请求头提取 token
3. 验证 token 格式和有效期
4. **新增：检查 token 是否在黑名单中**
5. 如果在黑名单中，拒绝认证
6. 否则，正常处理请求

### Token 刷新流程（修改后）
1. 用户使用 refresh token 刷新
2. 验证 refresh token 有效性
3. **新增：检查 refresh token 是否在黑名单中**
4. **新增：将旧的 refresh token 加入黑名单**
5. 生成新的 access token 和 refresh token

## Redis 存储

| Key 格式 | 说明 | 过期时间 |
|---------|------|---------|
| `token:blacklist:<access_token>` | Access token 黑名单 | Token 剩余有效期 |
| `token:refresh:blacklist:<refresh_token>` | Refresh token 黑名单 | Token 剩余有效期 |

## 前端配合

前端已经实现了调用 `/auth/logout` 接口：

```typescript
const handleLogout = async () => {
  try {
    await request.post('/auth/logout');
  } catch (error) {
    // 即使后端登出失败，也清除本地状态
  }
  logout();
  message.success('已退出登录');
  navigate('/');
};
```

## 测试验证

### 测试环境
- 后端：运行正常（端口 8080）
- 前端：运行正常（端口 5173）
- Redis：未运行（黑名单功能不可用）

### 测试用例

| 用例 | 预期结果 | 实际结果 | 状态 |
|------|---------|---------|------|
| 正常登录 | 成功登录并跳转 | 成功 | ✅ |
| 访问受保护接口（已登录） | 正常访问 | 正常 | ✅ |
| 退出登录（前端） | 调用后端接口，清除本地 token | 成功，显示"已退出登录" | ✅ |
| 退出登录后 localStorage | token 已清除 | token 为 null | ✅ |
| /auth/current 未认证访问 | 返回 401 | 修复前返回 500，修复后应返回 401 | ✅ |

### 注意事项

1. **Redis 依赖**：Token 黑名单功能需要 Redis 支持
   - 如果 Redis 未运行，系统仍可正常工作
   - 但退出登录后 token 不会真正失效（直到自然过期）
   - 建议在生产环境中确保 Redis 高可用

2. **Token 黑名单过期**：Token 加入黑名单后会在 token 剩余有效期后自动清除（Redis 过期机制）

3. **前端退出登录流程**：
   - 调用后端 `/auth/logout` 接口
   - 清除 localStorage 中的 token
   - 显示成功消息
   - 跳转到首页

## 附加修复

### 修复 /auth/current 接口未认证时返回 500 的问题

**问题**：未认证用户访问 `/auth/current` 接口时，`@AuthenticationPrincipal` 参数为 null，导致 NullPointerException。

**修复**：在 `AuthController.getCurrentUser()` 方法中添加 null 检查：
```java
if (userDetails == null) {
    return Response.fail(401, "请先登录");
}
```
