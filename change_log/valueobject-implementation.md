# ValueObject 实施日志

## 概述

本次更新在 DDD 项目中实现了值对象（Value Object）模式，将 email、phone、status 等字段从简单类型（String、Integer）重构为类型安全的值对象，提升了代码的可维护性和类型安全性。

**实施日期**: 2026-02-11
**实施内容**: 创建值对象、TypeHandler、JSON 序列化配置、MapStruct 转换器、重构实体、增强异常处理、编写单元测试

---

## 实施过程

### 阶段 1：创建值对象（领域层）

创建了以下值对象和相关类：

1. **BaseValueObject.java** - 值对象抽象基类，提供 equals/hashCode/sameValueAs 基础实现
2. **StatusType.java** - 状态类型接口
3. **Email.java** - 邮箱值对象，封装邮箱格式验证逻辑
4. **PhoneNumber.java** - 手机号值对象，封装手机号格式验证逻辑
5. **Status.java** - 泛型状态值对象，支持多种状态类型
6. **UserStatus.java** - 用户状态枚举（0-禁用，1-启用）
7. **RoleStatus.java** - 角色状态枚举（0-禁用，1-启用）
8. **PermissionStatus.java** - 权限状态枚举（0-禁用，1-启用）
9. **LoginStatus.java** - 登录状态枚举（0-失败，1-成功）

### 阶段 2：创建 TypeHandler（基础设施层）

创建了 6 个 MyBatis TypeHandler 来实现值对象与数据库的映射：

1. **EmailTypeHandler.java** - Email 与 VARCHAR 之间的转换
2. **PhoneNumberTypeHandler.java** - PhoneNumber 与 VARCHAR 之间的转换
3. **UserStatusTypeHandler.java** - Status<UserStatus> 与 TINYINT 之间的转换
4. **RoleStatusTypeHandler.java** - Status<RoleStatus> 与 TINYINT 之间的转换
5. **PermissionStatusTypeHandler.java** - Status<PermissionStatus> 与 TINYINT 之间的转换
6. **LoginStatusTypeHandler.java** - Status<LoginStatus> 与 TINYINT 之间的转换

### 阶段 3：配置 JSON 序列化（基础设施层）

创建了 JSON 序列化配置类：

1. **JacksonConfig.java** - Jackson 配置类，注册值对象的序列化/反序列化器
2. **EmailSerializer.java** - Email JSON 序列化器
3. **EmailDeserializer.java** - Email JSON 反序列化器
4. **PhoneNumberSerializer.java** - PhoneNumber JSON 序列化器
5. **PhoneNumberDeserializer.java** - PhoneNumber JSON 反序列化器
6. **StatusSerializer.java** - Status JSON 序列化器
7. **StatusDeserializer.java** - Status JSON 反序列化器

### 阶段 4：重构实体使用值对象（领域层）

修改了以下实体，将字段类型改为值对象：

1. **User.java**
   - `email`: String → Email
   - `phone`: String → PhoneNumber
   - `status`: Integer → Status<UserStatus>
   - 移除 `@Data` 注解，改用 `@Getter` + `@Setter`

2. **Role.java**
   - `status`: Integer → Status<RoleStatus>
   - 移除 `@Data` 注解，改用 `@Getter` + `@Setter`

3. **Permission.java**
   - `status`: Integer → Status<PermissionStatus>
   - 移除 `@Data` 注解，改用 `@Getter` + `@Setter`

4. **LoginLog.java**
   - `status`: Integer → Status<LoginStatus>
   - 移除 `@Data` 注解，改用 `@Getter` + `@Setter`

### 阶段 5：创建 MapStruct 转换器（接口层）

创建了 4 个 MapStruct 转换器：

1. **ValueObjectMapper.java** - 值对象与基本类型之间的通用映射方法
2. **UserConverter.java** - User 实体与 DTO 转换器
3. **RoleConverter.java** - Role 实体与 DTO 转换器
4. **PermissionConverter.java** - Permission 实体与 DTO 转换器
5. **LoginLogConverter.java** - LoginLog 实体与 DTO 转换器

### 阶段 6：修改控制器使用转换器（接口层）

修改了 **UserController.java**：
- 注入 `UserConverter`
- 替换所有 `BeanUtils.copyProperties()` 为 `converter` 方法
- 设置默认状态为启用

### 阶段 7：增强异常处理（接口层）

修改了 **GlobalExceptionHandler.java**：
- 添加 `IllegalArgumentException` 异常处理
- 将值对象验证失败的异常转换为 HTTP 400 响应

### 阶段 8：编写单元测试（测试层）

创建了 2 个单元测试类：

1. **EmailTest.java** - 测试 Email 值对象的各种场景
2. **PhoneNumberTest.java** - 测试 PhoneNumber 值对象的各种场景

---

## 架构原则

本次实施遵循以下架构原则：

### 依赖关系

```
ValueObject (领域层) → TypeHandler (基础设施层) → Entity (领域层) → Controller (接口层)
```

### 核心设计

- **DTO 层保持不变**：对外接口（Request/Response）继续使用 String/Integer 类型
- **实体层使用值对象**：领域实体使用类型安全的值对象
- **TypeHandler 桥接**：通过 MyBatis TypeHandler 实现值对象与数据库的映射
- **向后兼容**：API 接口、数据库结构无需变更

---

## 文件清单

### 创建的文件（32个）

#### 值对象（9个）
- `src/main/java/com/example/ddd/domain/model/valueobject/BaseValueObject.java`
- `src/main/java/com/example/ddd/domain/model/valueobject/StatusType.java`
- `src/main/java/com/example/ddd/domain/model/valueobject/Email.java`
- `src/main/java/com/example/ddd/domain/model/valueobject/PhoneNumber.java`
- `src/main/java/com/example/ddd/domain/model/valueobject/Status.java`
- `src/main/java/com/example/ddd/domain/model/valueobject/UserStatus.java`
- `src/main/java/com/example/ddd/domain/model/valueobject/RoleStatus.java`
- `src/main/java/com/example/ddd/domain/model/valueobject/PermissionStatus.java`
- `src/main/java/com/example/ddd/domain/model/valueobject/LoginStatus.java`

#### TypeHandler（6个）
- `src/main/java/com/example/ddd/infrastructure/persistence/handler/EmailTypeHandler.java`
- `src/main/java/com/example/ddd/infrastructure/persistence/handler/PhoneNumberTypeHandler.java`
- `src/main/java/com/example/ddd/infrastructure/persistence/handler/UserStatusTypeHandler.java`
- `src/main/java/com/example/ddd/infrastructure/persistence/handler/RoleStatusTypeHandler.java`
- `src/main/java/com/example/ddd/infrastructure/persistence/handler/PermissionStatusTypeHandler.java`
- `src/main/java/com/example/ddd/infrastructure/persistence/handler/LoginStatusTypeHandler.java`

#### JSON 序列化（7个）
- `src/main/java/com/example/ddd/infrastructure/config/JacksonConfig.java`
- `src/main/java/com/example/ddd/infrastructure/config/EmailSerializer.java`
- `src/main/java/com/example/ddd/infrastructure/config/EmailDeserializer.java`
- `src/main/java/com/example/ddd/infrastructure/config/PhoneNumberSerializer.java`
- `src/main/java/com/example/ddd/infrastructure/config/PhoneNumberDeserializer.java`
- `src/main/java/com/example/ddd/infrastructure/config/StatusSerializer.java`
- `src/main/java/com/example/ddd/infrastructure/config/StatusDeserializer.java`

#### MapStruct 转换器（5个）
- `src/main/java/com/example/ddd/interfaces/rest/converter/ValueObjectMapper.java`
- `src/main/java/com/example/ddd/interfaces/rest/converter/UserConverter.java`
- `src/main/java/com/example/ddd/interfaces/rest/converter/RoleConverter.java`
- `src/main/java/com/example/ddd/interfaces/rest/converter/PermissionConverter.java`
- `src/main/java/com/example/ddd/interfaces/rest/converter/LoginLogConverter.java`

#### 测试类（2个）
- `src/test/java/com/example/ddd/domain/model/valueobject/EmailTest.java`
- `src/test/java/com/example/ddd/domain/model/valueobject/PhoneNumberTest.java`

### 修改的文件（6个）

1. `src/main/java/com/example/ddd/domain/model/entity/User.java`
2. `src/main/java/com/example/ddd/domain/model/entity/Role.java`
3. `src/main/java/com/example/ddd/domain/model/entity/Permission.java`
4. `src/main/java/com/example/ddd/domain/model/entity/LoginLog.java`
5. `src/main/java/com/example/ddd/interfaces/rest/controller/UserController.java`
6. `src/main/java/com/example/ddd/interfaces/rest/exception/GlobalExceptionHandler.java`

---

## 验证与测试

### 单元测试

已创建 Email 和 PhoneNumber 的单元测试，覆盖以下场景：

**EmailTest**:
- 有效邮箱创建成功
- null 邮箱抛出异常
- 空邮箱抛出异常
- 无效格式邮箱抛出异常
- equals/hashCode 正确工作
- ofNullable 方法正常工作
- sameValueAs 方法正常工作

**PhoneNumberTest**:
- 有效手机号创建成功（13-19开头）
- null 手机号抛出异常
- 空手机号抛出异常
- 无效格式手机号抛出异常
- 长度不正确的手机号抛出异常
- equals/hashCode 正确工作
- ofNullable 方法正常工作
- sameValueAs 方法正常工作

### 功能验证

可以使用以下 API 进行功能验证：

1. **创建用户**：
   ```bash
   POST /api/users
   {
     "username": "testuser",
     "password": "password123",
     "email": "test@example.com",
     "phone": "13800138000"
   }
   ```

2. **无效邮箱测试**：
   ```bash
   POST /api/users
   {
     "email": "invalid-email"
   }
   ```
   预期：返回 400，错误信息"邮箱格式不正确"

---

## 向后兼容性

### API 接口：✅ 无需变更
- 请求：JSON 中 email/phone/status 仍然是字符串/整数
- 响应：JSON 序列化自动转换为字符串/整数

### 数据库：✅ 无需变更
- email: VARCHAR(100)
- phone: VARCHAR(20)
- status: TINYINT

---

## 风险与缓解

### 风险 1：现有数据不符合验证规则
**缓解**：TypeHandler 中捕获异常，返回 null 并记录警告日志

### 风险 2：MyBatis Plus 查询构造器兼容性
**缓解**：使用字符串字段名：`queryWrapper.eq("email", "test@example.com")`

---

## 后续工作

1. 修改 RoleController、PermissionController 使用转换器
2. 运行集成测试验证所有功能
3. 性能测试对比
4. 更新 API 文档

---

## 总结

本次 ValueObject 实施成功完成了以下目标：

1. ✅ 创建了常用值对象（Email、PhoneNumber、Status）来封装验证逻辑
2. ✅ 重构了 User、Role、Permission、LoginLog 实体使用值对象
3. ✅ 保持了 API 接口兼容性，无需修改数据库表结构
4. ✅ 创建了 MapStruct 转换器实现 DTO 与 Entity 的转换
5. ✅ 增强了异常处理，统一处理值对象验证失败的情况
6. ✅ 编写了单元测试，确保值对象功能正确

通过值对象的引入，项目的类型安全性得到了提升，验证逻辑更加集中，代码可维护性也得到了改善。
