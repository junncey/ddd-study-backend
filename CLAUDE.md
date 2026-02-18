# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 重要说明

**当前运行环境使用的是 GLM 大模型，而非 Claude 大模型。** 请在生成代码和回复时注意模型能力的差异。

## 交流语言

**重要**：在此项目中与用户的所有交流必须使用中文。所有回复、解释和文档都应使用中文。

## 开发日志管理

**重要规则**：所有的开发日志、变更记录、Bug 修复过程、测试报告等都必须记录在 `change_log/` 目录中。

## 端口配置规则

**重要规则**：未经用户明确允许，禁止修改后端服务端口配置。

- 后端默认端口：8080
- 前端默认端口：5173
- 数据库端口：3307

## 项目概述

基于领域驱动设计（DDD）的电商项目示例，采用六边形架构（Hexagonal Architecture）。

## 技术栈

**后端：**
- Java 17、Spring Boot 3.5.10、MyBatis Plus 3.5.5
- MySQL 8.0、Redis、Druid 连接池
- Lombok、MapStruct、Hutool
- SpringDoc OpenAPI（Swagger）、Spring Security + JWT

**前端：**
- React 19、TypeScript、Vite
- Ant Design、TanStack Query、Zustand

## 常用命令

### 后端
```bash
mvn clean compile          # 编译
mvn spring-boot:run        # 启动（默认8080端口）
mvn clean package          # 打包
mvn test                   # 运行测试
mvn test -Dtest=EmailTest  # 运行单个测试
```

### 前端
```bash
cd frontend
npm run dev                # 启动开发服务器
npm run build              # 构建生产版本
npm run lint               # 代码检查
```

### 数据库
```bash
mysql -P 3307 -u root -proot ddd_demo < src/main/resources/db/migration/V1.0.0__ecommerce_tables.sql
```

## 六边形架构

依赖关系：`interfaces → application → domain ← infrastructure`

### 架构层次

1. **domain（领域层）** - 核心业务逻辑，不依赖任何其他层
   - `entity/` - 领域实体（继承 `BaseEntity`）
   - `valueobject/` - 值对象（实现 `ValueObject` 接口，如 `Money`、`Quantity`、`OrderStatus`）
   - `repository/` - 仓储接口（端口 Port）
   - `service/` - 领域服务（继承 `DomainService`）

2. **application（应用层）** - 用例编排，依赖领域层
   - 继承 `ApplicationService` 基类
   - 调用领域服务完成业务用例

3. **interfaces（接口层）** - 主适配器（Driving Adapter）
   - `rest/controller/` - REST 控制器
   - `rest/dto/` - 数据传输对象
   - `rest/vo/` - 视图对象（统一响应 `Response<T>`）
   - `rest/exception/` - 异常处理

4. **infrastructure（基础设施层）** - 从适配器（Driven Adapter）
   - `persistence/mapper/` - MyBatis Mapper（继承 `BaseMapper<T>`）
   - `persistence/repository/` - 仓储实现（实现领域层定义的接口）
   - `persistence/handler/` - MyBatis TypeHandler（值对象序列化）
   - `config/` - 配置类
   - `security/` - 安全相关（JWT、过滤器、权限校验）

### 核心概念

- **端口（Port）**：定义在领域层的接口，如 `UserRepository extends BaseRepository<User>`
- **适配器（Adapter）**：实现端口的具体类，如 `UserRepositoryImpl implements UserRepository`

## 添加新实体的开发步骤

1. **创建实体** - `domain/model/entity/` 创建继承 `BaseEntity` 的实体类，添加 `@TableName` 注解
2. **创建仓储接口** - `domain/repository/` 创建继承 `BaseRepository<T>` 的接口
3. **创建 Mapper** - `infrastructure/persistence/mapper/` 创建继承 `BaseMapper<T>` 的接口，添加 `@Mapper` 注解
4. **创建仓储实现** - `infrastructure/persistence/repository/` 创建实现类，添加 `@Repository` 注解
5. **创建领域服务** - `domain/service/` 创建继承 `DomainService` 的服务类
6. **创建应用服务** - `application/service/` 创建继承 `ApplicationService` 的服务类
7. **创建控制器和 DTO** - `interfaces/rest/` 下创建对应的控制器和 DTO

## 值对象处理

项目使用值对象模式封装业务概念：
- `Money` - 金额（使用 `BigDecimal`）
- `Quantity` - 数量
- `OrderStatus`、`ProductStatus`、`PaymentStatus` 等 - 状态枚举

**TypeHandler 序列化**：值对象在数据库中以 JSON 格式存储，通过自定义 `TypeHandler` 处理。

**前端处理**：后端返回的值对象可能是 `{value: xxx}` 或字符串格式，前端需要使用辅助函数提取值：
```typescript
const extractNumber = (val: unknown): number => {
  if (val === null || val === undefined) return 0;
  if (typeof val === 'number') return val;
  if (typeof val === 'object' && val !== null && 'value' in val) {
    return Number((val as { value: number }).value);
  }
  return 0;
};
```

## 数据库配置

- 数据库端口：3307
- 数据库名：ddd_demo
- 表前缀：t_
- 主键策略：自增
- 逻辑删除：deleted 字段（0-未删除，1-已删除）

## API 访问

- API 前缀：`/api`（配置在 `server.servlet.context-path`）
- Swagger UI：http://localhost:8080/api/swagger-ui.html
- Druid 监控：http://localhost:8080/api/druid（账号：admin/admin）

## 认证机制

使用 JWT 认证，请求头格式：`Authorization: Bearer <token>`

前端 Token 刷新机制已实现，存储在 localStorage。

## Git 仓库管理

### 仓库结构

本项目采用**前后端分离的 Git 仓库策略**：

| 仓库 | 位置 | 内容 |
|------|------|------|
| 后端仓库 | `D:/ddd-study2` | Java 后端代码、文档、配置 |
| 前端仓库 | `D:/ddd-study2/frontend/` | React 前端代码（独立 Git 仓库） |

### 提交规范

**重要规则**：前端代码和后端代码必须分开提交到各自的仓库。

#### 提交信息格式

```
<type>: <subject>

<body>

Co-Authored-By: Claude <noreply@anthropic.com>
```

#### Type 类型

| Type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档更新 |
| `style` | 代码格式（不影响功能） |
| `refactor` | 重构（不是新功能也不是修复） |
| `perf` | 性能优化 |
| `test` | 测试相关 |
| `chore` | 构建配置、依赖更新等 |

#### 提交步骤

**后端代码提交**（在项目根目录）：
```bash
cd D:/ddd-study2
git add .
git commit -m "feat: 功能描述"
```

**前端代码提交**（在 frontend 目录）：
```bash
cd D:/ddd-study2/frontend
git add .
git commit -m "feat: 功能描述"
```

### 代码修改后提交流程

**重要规则**：代码修改完成后，必须按以下流程提交：

1. **确认修改内容**：使用 `git status` 和 `git diff` 查看修改的文件和内容
2. **分开提交**：前端和后端代码必须分别提交到各自的仓库
3. **记录变更日志**：重要的修改（Bug 修复、新功能、重构等）需要在 `change_log/` 目录下创建日志文件
4. **规范提交信息**：使用规范的 commit message 格式

#### 完整提交流程示例

```bash
# 1. 查看修改状态
git status
git diff

# 2. 添加文件（推荐指定具体文件，避免添加不需要的文件）
git add src/main/java/com/example/ddd/domain/xxx.java

# 3. 提交（包含变更说明和协作者标记）
git commit -m "$(cat <<'EOF'
fix: 简短描述修复的问题

- 具体修改点1
- 具体修改点2

Co-Authored-By: Claude <noreply@anthropic.com>
EOF
)"

# 4. 确认提交成功
git log -1
```

### 注意事项

1. 后端仓库的 `.gitignore` 已配置忽略 `frontend/` 目录
2. 前端仓库是独立的 Git 仓库，有自己的 `.git`
3. 不要在后端仓库中提交前端代码
4. 提交前确保代码已通过编译/构建
5. 重要修改需同步更新 `change_log/` 目录下的日志文件

