# DDD Demo Project

基于领域驱动设计（DDD）的 Java 项目示例，采用六边形架构。

## 技术栈

- **Java 17**
- **Spring Boot 3.5.10**
- **MyBatis Plus 3.5.5** - ORM 框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存
- **Druid** - 数据库连接池
- **Lombok** - 简化代码
- **MapStruct** - 对象映射
- **Hutool** - 工具类库
- **SpringDoc OpenAPI** - API 接口文档（Swagger）
- **Spring Security** - 安全框架
- **JWT** - JSON Web Token 认证
- **Druid** - 数据库监控

## 项目结构

项目采用六边形架构，包含以下层次：

```
ddd-demo/
├── src/main/java/com/example/ddd/
│   ├── DddApplication.java           # 启动类
│   │
│   ├── domain/                        # 领域层（核心业务）
│   │   ├── model/                     # 领域模型
│   │   │   ├── entity/                # 实体
│   │   │   │   ├── BaseEntity.java   # 基础实体
│   │   │   │   └── User.java         # 用户实体
│   │   │   └── valueobject/           # 值对象
│   │   │       └── ValueObject.java  # 值对象接口
│   │   ├── repository/                # 仓储接口（端口 Port）
│   │   │   ├── BaseRepository.java   # 基础仓储接口
│   │   │   └── UserRepository.java   # 用户仓储接口
│   │   └── service/                   # 领域服务
│   │       ├── DomainService.java    # 领域服务基类
│   │       └── UserDomainService.java # 用户领域服务
│   │
│   ├── application/                   # 应用层（用例编排）
│   │   ├── ApplicationService.java   # 应用服务基类
│   │   └── service/
│   │       └── UserApplicationService.java # 用户应用服务
│   │
│   ├── infrastructure/                # 基础设施层（适配器 Adapter）
│   │   ├── config/                    # 配置类
│   │   │   ├── RedisConfig.java      # Redis 配置
│   │   │   └── MybatisPlusConfig.java # MyBatis Plus 配置
│   │   └── persistence/               # 持久化
│   │       ├── mapper/                # MyBatis Mapper
│   │       │   └── UserMapper.java   # 用户 Mapper
│   │       └── repository/            # 仓储实现
│   │           └── UserRepositoryImpl.java # 用户仓储实现
│   │
│   └── interfaces/                    # 接口层（主适配器 Driving Adapter）
│       └── rest/                      # REST API
│           ├── controller/            # 控制器
│           │   └── UserController.java # 用户控制器
│           ├── dto/                   # 数据传输对象
│           │   ├── UserCreateRequest.java
│           │   ├── UserUpdateRequest.java
│           │   └── UserResponse.java
│           ├── vo/                    # 视图对象
│           │   └── Response.java      # 统一响应
│           └── exception/             # 异常处理
│               ├── GlobalExceptionHandler.java
│               └── BusinessException.java
│
└── src/main/resources/
    ├── application.yml                # 应用配置
    └── db/
        └── schema.sql                 # 数据库脚本
```

## 六边形架构说明

### 核心概念

1. **端口（Port）**：定义在领域层中的接口，如 `UserRepository`
2. **适配器（Adapter）**：实现端口的具体类，如 `UserRepositoryImpl`
3. **主适配器（Driving Adapter）**：驱动应用执行的适配器，如 REST 控制器
4. **从适配器（Driven Adapter）**：被应用调用的适配器，如数据库访问

### 依赖规则

```
interfaces → application → domain ← infrastructure
```

- **Domain（领域层）**：不依赖任何其他层，包含核心业务逻辑
- **Application（应用层）**：依赖领域层，编排用例
- **Interfaces（接口层）**：依赖应用层，处理外部请求
- **Infrastructure（基础设施层）**：实现领域层定义的接口

## 快速开始

### 1. 环境准备

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 初始化数据库

执行数据库脚本：

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/ddd_demo
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

或直接运行 `DddApplication` 主类。

### 5. 测试 API

#### 创建用户
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123",
    "email": "test@example.com",
    "phone": "13800138000",
    "nickname": "测试用户"
  }'
```

#### 查询用户
```bash
curl http://localhost:8080/api/users/1
```

#### 分页查询
```bash
curl "http://localhost:8080/api/users/page?current=1&size=10"
```

## 主要特性

### 1. 基础实体

所有实体继承 `BaseEntity`，自动包含：
- 主键 ID
- 创建时间、更新时间
- 创建人、更新人
- 逻辑删除标记

### 2. 自动填充

MyBatis Plus 自动填充：
- `createTime`、`updateTime`：当前时间
- `createBy`、`updateBy`：当前用户（需集成安全框架）

### 3. 缓存支持

使用 Spring Cache + Redis：
- 方法级缓存注解 `@Cacheable`、`@CacheEvict`
- 支持自定义缓存策略

### 4. 统一异常处理

全局异常处理器捕获：
- 业务异常 `BusinessException`
- 参数校验异常
- 系统异常

### 5. 分页查询

集成 MyBatis Plus 分页插件：
- `Page<User>` 分页对象
- 支持自定义分页参数

## 开发指南

### 添加新实体

1. 创建实体类继承 `BaseEntity`
2. 创建仓储接口继承 `BaseRepository<T>`
3. 创建 Mapper 接口继承 `BaseMapper<T>`
4. 创建仓储实现继承 `ServiceImpl<Mapper, Entity>`
5. 创建领域服务继承 `DomainService`
6. 创建应用服务继承 `ApplicationService`
7. 创建控制器和 DTO

### 示例代码

```java
// 1. 实体
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product")
public class Product extends BaseEntity {
    private String name;
    private BigDecimal price;
}

// 2. 仓储接口
public interface ProductRepository extends BaseRepository<Product> {
    default Product findByName(String name) {
        return lambdaQuery()
                .eq(Product::getName, name)
                .one();
    }
}

// 3. Mapper
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}

// 4. 仓储实现
@Repository
public class ProductRepositoryImpl
    extends ServiceImpl<ProductMapper, Product>
    implements ProductRepository {
}

// 5. 领域服务
@Service
@RequiredArgsConstructor
public class ProductDomainService extends DomainService {
    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        validate(); // 业务规则验证
        productRepository.save(product);
        return product;
    }
}

// 6. 应用服务
@Service
@RequiredArgsConstructor
public class ProductApplicationService extends ApplicationService {
    private final ProductDomainService productDomainService;

    public Product createProduct(Product product) {
        beforeExecute();
        try {
            return productDomainService.createProduct(product);
        } finally {
            afterExecute();
        }
    }
}
```

## API 文档

### Swagger UI

访问 Swagger UI 在线接口文档：http://localhost:8080/api/swagger-ui.html

或者在浏览器中打开：http://localhost:8080/api/swagger-ui/index.html

### 使用 JWT 认证

1. 调用 `/api/auth/login` 接口获取 Token
2. 点击 Swagger UI 右上角的 **Authorize** 按钮
3. 在弹出框中输入 Token（格式：`Bearer your-token-here`）
4. 点击 **Authorize** 确认
5. 之后的所有请求都会自动携带认证信息

### OpenAPI JSON

访问 OpenAPI JSON 规范：http://localhost:8080/api/v3/api-docs

可用于生成客户端 SDK 或集成到其他文档平台。

## 数据库监控

访问 Druid 监控面板：http://localhost:8080/api/druid

默认账号：
- 用户名：`admin`
- 密码：`admin`