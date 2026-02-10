# Spring Security 集成开发日志

## 日期
2024-02-10

## 变更说明
在项目中集成 Spring Security 和 JWT，实现完整的用户认证授权功能。

## 变更内容

### 1. 依赖配置
- 在 `pom.xml` 中添加 Spring Security 和 JWT 相关依赖：
  - `spring-boot-starter-security`
  - `jjwt-api`、`jjwt-impl`、`jjwt-jackson`

### 2. 核心安全组件

#### JWT 工具类 (`infrastructure/security/JwtUtil.java`)
- 生成和验证 JWT token
- 支持 Access Token 和 Refresh Token
- Token 过期时间配置

#### 用户详情实现 (`infrastructure/security/UserDetailsImpl.java`)
- 实现 Spring Security 的 UserDetails 接口
- 封装用户认证信息

#### 用户详情服务 (`infrastructure/security/UserDetailsServiceImpl.java`)
- 实现 UserDetailsService 接口
- 从数据库加载用户信息

#### JWT 认证过滤器 (`infrastructure/security/JwtAuthenticationFilter.java`)
- 从请求头中提取 JWT token
- 验证并设置用户认证信息

#### 认证入口点和访问拒绝处理器
- `JwtAuthenticationEntryPoint.java`: 处理认证失败，返回 401
- `JwtAccessDeniedHandler.java`: 处理权限不足，返回 403

### 3. 安全配置

#### Spring Security 配置 (`infrastructure/config/SecurityConfig.java`)
- 密码编码器配置（BCrypt）
- 无状态会话管理（Stateless）
- CORS 跨域配置
- 公开端点配置（无需认证的接口）
- JWT 过滤器链配置

### 4. 验证码功能

#### 验证码工具类 (`infrastructure/security/CaptchaUtil.java`)
- 生成图形验证码
- 添加干扰线和干扰点

#### 验证码应用服务 (`application/service/CaptchaApplicationService.java`)
- 生成验证码并存储到 Redis
- 验证码校验

### 5. 登录日志功能

#### 登录日志实体 (`domain/model/entity/LoginLog.java`)
- 记录用户登录信息
- 记录登录 IP、浏览器、操作系统等

#### 登录日志服务 (`domain/service/LoginLogDomainService.java`)
- 记录登录成功/失败日志

#### 工具类
- `IpUtil.java`: 获取客户端 IP 地址
- `UserAgentUtil.java`: 解析浏览器和操作系统信息

### 6. 认证服务

#### 认证领域服务 (`domain/service/AuthDomainService.java`)
- 验证用户登录信息
- 用户注册逻辑
- 检查用户名/邮箱是否存在

#### 认证应用服务 (`application/service/AuthApplicationService.java`)
- 用户登录（生成 JWT token）
- 用户注册
- 刷新 token
- 用户登出
- 记录登录日志

### 7. API 接口

#### 认证控制器 (`interfaces/rest/controller/AuthController.java`)
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/refresh` - 刷新 Token
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/current` - 获取当前用户信息
- `GET /api/auth/captcha` - 获取验证码

### 8. DTO/VO

#### 认证相关 DTO (`interfaces/rest/dto/auth/`)
- `LoginRequest.java` - 登录请求
- `RegisterRequest.java` - 注册请求
- `RefreshTokenRequest.java` - 刷新 Token 请求
- `LoginResponse.java` - 登录响应
- `CaptchaResponse.java` - 验证码响应

### 9. 数据库设计

#### 新增表
- `t_role` - 角色表
- `t_permission` - 权限表
- `t_user_role` - 用户角色关联表
- `t_role_permission` - 角色权限关联表
- `t_login_log` - 登录日志表

#### 实体类
- `Role.java` - 角色实体
- `Permission.java` - 权限实体
- `UserRole.java` - 用户角色关联实体
- `RolePermission.java` - 角色权限关联实体

### 10. 配置文件

#### application.yml 新增配置
```yaml
# JWT 配置
jwt:
  secret: ddd-demo-jwt-secret-key-2024-spring-boot-security
  expiration: 604800000  # 7天
  refresh-expiration: 2592000000  # 30天
```

## 测试账号

默认管理员账号：
- 用户名: `admin`
- 密码: `admin123`

## API 使用示例

### 1. 获取验证码
```bash
GET http://localhost:8080/api/auth/captcha
```

### 2. 用户登录
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "captchaCode": "验证码",
  "captchaKey": "验证码Key"
}
```

响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      ...
    }
  }
}
```

### 3. 访问受保护接口
```bash
GET http://localhost:8080/api/users/1
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 后续优化建议

1. **权限加载优化**: 实现从数据库加载用户的角色和权限信息
2. **Token 黑名单**: 使用 Redis 实现登出时的 Token 黑名单机制
3. **记住我功能**: 实现记住登录状态功能
4. **多终端登录**: 支持同一用户多设备同时登录
5. **IP 白名单**: 实现基于 IP 的访问控制
6. **限流防刷**: 实现登录接口的限流和防刷机制
7. **密码强度**: 添加密码强度校验
8. **邮箱验证**: 实现注册邮箱验证功能

## 注意事项

1. JWT 密钥在生产环境中应使用更复杂的密钥，并从环境变量中读取
2. 默认用户密码应立即修改
3. Redis 需要正常运行，验证码功能才可用
4. 数据库需要执行 schema.sql 初始化表结构和数据
