# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

基于领域驱动设计（DDD）的电商项目示例，采用六边形架构（Hexagonal Architecture）。

## 技术栈

**后端**：Java 17、Spring Boot 3.5.10、MyBatis Plus 3.5.5、MySQL 8.0（端口3307）、Redis、Spring Security + JWT

**前端**：React 19、TypeScript、Vite、Ant Design、TanStack Query、Zustand

## 端口配置

- 后端：8080
- 前端：5173
- 数据库：3307

**重要**：未经用户明确允许，禁止修改端口配置。

## 常用命令

### 后端
```bash
mvn clean compile          # 编译
mvn spring-boot:run        # 启动
mvn test -Dtest=EmailTest  # 运行单个测试
```

### 前端
```bash
cd frontend && npm run dev   # 启动开发服务器
cd frontend && npm run build # 构建
```

## 六边形架构

依赖关系：`interfaces → application → domain ← infrastructure`

### 层次结构

1. **domain（领域层）** - 核心业务逻辑，不依赖任何其他层
   - `entity/` - 继承 `BaseEntity`
   - `valueobject/` - 实现 `ValueObject` 接口
   - `repository/` - 仓储接口（端口 Port）
   - `service/` - 继承 `DomainService`

2. **application（应用层）** - 用例编排，依赖领域层
   - 继承 `ApplicationService` 基类，调用领域服务

3. **interfaces（接口层）** - 主适配器（Driving Adapter）
   - `rest/controller/`、`rest/dto/`、`rest/vo/`（统一响应 `Response<T>`）

4. **infrastructure（基础设施层）** - 从适配器（Driven Adapter）
   - `persistence/mapper/` - 继承 `BaseMapper<T>`
   - `persistence/repository/` - 实现领域层接口
   - `persistence/handler/` - MyBatis TypeHandler
   - `config/`、`security/`

### 添加新实体步骤

1. 创建实体 `domain/model/entity/` 继承 `BaseEntity`
2. 创建仓储接口 `domain/repository/` 继承 `BaseRepository<T>`
3. 创建 Mapper `infrastructure/persistence/mapper/` 继承 `BaseMapper<T>`
4. 创建仓储实现 `infrastructure/persistence/repository/`
5. 创建领域服务 `domain/service/` 继承 `DomainService`
6. 创建应用服务 `application/service/` 继承 `ApplicationService`
7. 创建控制器和 DTO `interfaces/rest/`

## 值对象处理

值对象（`Money`、`Quantity`、`OrderStatus` 等）在数据库中以 JSON 格式存储。

前端需要提取值对象的值：
```typescript
const extractValue = (val: unknown): number => {
  if (val === null || val === undefined) return 0;
  if (typeof val === 'number') return val;
  if (typeof val === 'object' && val !== null && 'value' in val) {
    return Number((val as { value: number }).value);
  }
  return 0;
};
```

## Git 仓库管理

本项目前后端代码分离：
- 后端仓库：项目根目录
- 前端仓库：`frontend/` 目录（独立 Git 仓库）

**规则**：前端和后端代码必须分开提交到各自仓库。

### 提交格式
```
<type>: <subject>

Co-Authored-By: Claude <noreply@anthropic.com>
```

## 开发日志

所有开发日志、变更记录必须在 `change_log/` 目录中记录。

## 测试要求

功能修改完成后必须进行测试验证：
1. 编译验证
2. 服务启动验证
3. 功能测试
4. 记录测试结果到变更日志

## 服务启动前准备

启动新服务前，必须先停止已运行的进程避免端口冲突：

**Windows**:
```bash
netstat -ano | findstr :8080    # 检查端口
taskkill //F //PID <PID>         # 停止进程
```

## API 访问

- API 前缀：`/api`
- Swagger UI：http://localhost:8080/api/swagger-ui.html
- Druid 监控：http://localhost:8080/api/druid（admin/admin）
- JWT 认证头：`Authorization: Bearer <token>`
