# Role 和 Permission 功能实现开发日志

## 日期
2025-02-10

## 变更说明
实现完整的角色（Role）和权限（Permission）管理功能，包括仓储层、服务层和接口层的完整实现。

## 变更内容

### 1. 领域层（Domain）

#### 1.1 仓储接口（Repository）

**新增文件：** `domain/repository/RoleRepository.java`
- 继承 `BaseRepository<Role>`
- 新增方法：
  - `findByRoleCode(String roleCode)` - 根据角色编码查询角色
  - `existsByRoleCode(String roleCode)` - 检查角色编码是否存在

**新增文件：** `domain/repository/PermissionRepository.java`
- 继承 `BaseRepository<Permission>`
- 新增方法：
  - `findByPermissionCode(String permissionCode)` - 根据权限编码查询权限
  - `existsByPermissionCode(String permissionCode)` - 检查权限编码是否存在
  - `findByParentId(Long parentId)` - 根据父权限ID查询子权限列表
  - `findAll()` - 查询所有权限
  - `findByPermissionType(Integer permissionType)` - 根据权限类型查询权限

#### 1.2 领域服务（Domain Service）

**新增文件：** `domain/service/RoleDomainService.java`
- 继承 `DomainService`
- 核心方法：
  - `createRole(Role)` - 创建角色，包含角色编码唯一性验证
  - `updateRole(Role)` - 更新角色，检查角色编码冲突
  - `deleteRole(Long)` - 删除角色

**新增文件：** `domain/service/PermissionDomainService.java`
- 继承 `DomainService`
- 核心方法：
  - `createPermission(Permission)` - 创建权限，包含权限编码唯一性验证
  - `updatePermission(Permission)` - 更新权限，防止循环父级引用
  - `deletePermission(Long)` - 删除权限，检查是否有子权限
  - `getPermissionTree()` - 获取权限树
  - `buildTree()` - 构建树形结构（私有方法）

### 2. 基础设施层（Infrastructure）

#### 2.1 Mapper 接口

**新增文件：** `infrastructure/persistence/mapper/RoleMapper.java`
- 继承 `BaseMapper<Role>`
- 添加 `@Mapper` 注解

**新增文件：** `infrastructure/persistence/mapper/PermissionMapper.java`
- 继承 `BaseMapper<Permission>`
- 添加 `@Mapper` 注解

#### 2.2 仓储实现（Repository Implementation）

**新增文件：** `infrastructure/persistence/repository/RoleRepositoryImpl.java`
- 实现 `RoleRepository` 接口
- 使用 `LambdaQueryWrapper` 实现查询
- 注入 `RoleMapper` 完成数据访问

**新增文件：** `infrastructure/persistence/repository/PermissionRepositoryImpl.java`
- 实现 `PermissionRepository` 接口
- 实现树形结构查询（按 sort 排序）
- 注入 `PermissionMapper` 完成数据访问

### 3. 应用层（Application）

**新增文件：** `application/service/RoleApplicationService.java`
- 继承 `ApplicationService`
- 用例编排方法：
  - `createRole(Role)` - 创建角色用例，清除缓存
  - `updateRole(Role)` - 更新角色用例
  - `deleteRole(Long)` - 删除角色用例
  - `getRoleById(Long)` - 查询角色用例，支持缓存
  - `getRoleByRoleCode(String)` - 根据角色编码查询
  - `pageRoles(Long, Long)` - 分页查询角色
- 缓存配置：使用 `@Cacheable` 和 `@CacheEvict`

**新增文件：** `application/service/PermissionApplicationService.java`
- 继承 `ApplicationService`
- 用例编排方法：
  - `createPermission(Permission)` - 创建权限用例
  - `updatePermission(Permission)` - 更新权限用例
  - `deletePermission(Long)` - 删除权限用例
  - `getPermissionById(Long)` - 查询权限用例
  - `getPermissionByPermissionCode(String)` - 根据权限编码查询
  - `pagePermissions(Long, Long)` - 分页查询权限
  - `getPermissionTree()` - 查询权限树
  - `getPermissionsByParentId(Long)` - 根据父ID查询子权限
  - `getPermissionsByType(Integer)` - 根据类型查询权限

### 4. 接口层（Interfaces）

#### 4.1 请求 DTO（Request DTO）

**新增文件：** `interfaces/rest/dto/RoleCreateRequest.java`
- 字段：roleCode, roleName, description, sort, status
- 验证注解：
  - `@NotBlank` - 角色编码、名称不能为空
  - `@Pattern` - 角色编码只能包含大写字母和下划线
  - `@Size` - 长度限制

**新增文件：** `interfaces/rest/dto/RoleUpdateRequest.java`
- 字段：id, roleCode, roleName, description, sort, status
- 验证注解：`@NotNull` - ID不能为空

**新增文件：** `interfaces/rest/dto/PermissionCreateRequest.java`
- 字段：parentId, permissionCode, permissionName, permissionType, path, component, icon, sort, visible, status
- 验证注解：完整的非空和长度验证

**新增文件：** `interfaces/rest/dto/PermissionUpdateRequest.java`
- 字段：包含所有权限字段（可选更新）
- 验证注解：`@NotNull` - ID不能为空

#### 4.2 响应 DTO（Response DTO）

**新增文件：** `interfaces/rest/dto/RoleResponse.java`
- 字段：id, roleCode, roleName, description, sort, status, createTime, updateTime
- 实现 `Serializable` 接口
- 时间字段使用 `@JsonFormat` 格式化

**新增文件：** `interfaces/rest/dto/PermissionResponse.java`
- 字段：id, parentId, permissionCode, permissionName, permissionType, permissionTypeDesc, path, component, icon, sort, visible, status, createTime, updateTime
- 包含权限类型描述字段（permissionTypeDesc）

#### 4.3 控制器（Controller）

**新增文件：** `interfaces/rest/controller/RoleController.java`
- 路由：`/roles`
- 端点：
  - `POST /roles` - 创建角色
  - `PUT /roles` - 更新角色
  - `DELETE /roles/{id}` - 删除角色
  - `GET /roles/{id}` - 查询角色
  - `GET /roles/code/{roleCode}` - 根据编码查询
  - `GET /roles/page` - 分页查询
- 添加 Swagger 注解：`@Tag`, `@Operation`

**新增文件：** `interfaces/rest/controller/PermissionController.java`
- 路由：`/permissions`
- 端点：
  - `POST /permissions` - 创建权限
  - `PUT /permissions` - 更新权限
  - `DELETE /permissions/{id}` - 删除权限
  - `GET /permissions/{id}` - 查询权限
  - `GET /permissions/code/{permissionCode}` - 根据编码查询
  - `GET /permissions/page` - 分页查询
  - `GET /permissions/tree` - 查询权限树
  - `GET /permissions/children/{parentId}` - 查询子权限
  - `GET /permissions/type/{permissionType}` - 根据类型查询
- 辅助方法：`toResponse()` - 实体转DTO，包含类型描述转换

## API 端点清单

### 角色管理 API
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/roles | 创建角色 |
| PUT | /api/roles | 更新角色 |
| DELETE | /api/roles/{id} | 删除角色 |
| GET | /api/roles/{id} | 查询角色 |
| GET | /api/roles/code/{roleCode} | 根据编码查询 |
| GET | /api/roles/page | 分页查询 |

### 权限管理 API
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/permissions | 创建权限 |
| PUT | /api/permissions | 更新权限 |
| DELETE | /api/permissions/{id} | 删除权限 |
| GET | /api/permissions/{id} | 查询权限 |
| GET | /api/permissions/code/{permissionCode} | 根据编码查询 |
| GET | /api/permissions/page | 分页查询 |
| GET | /api/permissions/tree | 查询权限树 |
| GET | /api/permissions/children/{parentId} | 查询子权限 |
| GET | /api/permissions/type/{permissionType} | 根据类型查询 |

## 架构特点

### 1. 六边形架构实践
- 领域层定义仓储接口（Port）
- 基础设施层实现仓储接口（Adapter）
- 依赖反转：domain 不依赖 infrastructure

### 2. 分层职责清晰
- **领域服务**：核心业务规则验证
- **应用服务**：用例编排、缓存管理
- **控制器**：HTTP 请求处理、DTO 转换

### 3. 业务规则
- 角色编码唯一性校验
- 权限编码唯一性校验
- 权限树形结构维护
- 防止循环父级引用
- 删除前检查子权限

### 4. 缓存策略
- 角色列表：`roles`
- 单个角色：`role`（按 ID 或编码）
- 权限列表：`permissions`
- 单个权限：`permission`（按 ID 或编码）

## 测试建议

### 1. 单元测试
- `RoleDomainService` - 业务规则验证
- `PermissionDomainService` - 树形结构构建
- 缓存效果验证

### 2. 集成测试
- CRUD 操作完整流程
- 树形结构查询正确性
- 并发场景下的编码唯一性

### 3. API 测试
- 使用 Swagger UI 测试所有端点
- 验证参数校验规则
- 验证缓存清除策略

## 后续优化建议

1. **关联关系管理**：
   - 实现用户-角色分配功能
   - 实现角色-权限分配功能
   - 添加批量操作接口

2. **权限树优化**：
   - 支持多级权限拖拽排序
   - 添加权限树缓存优化
   - 支持权限树展开/折叠状态

3. **数据权限**：
   - 实现基于角色的数据权限过滤
   - 添加权限继承机制

4. **审计日志**：
   - 记录角色/权限变更历史
   - 支持操作回溯

5. **批量操作**：
   - 批量创建/删除角色
   - 批量分配权限
   - 权限导入/导出

## 注意事项

1. 数据库表 `t_role` 和 `t_permission` 需要提前创建
2. 角色编码建议使用大写字母和下划线（如：ADMIN、USER）
3. 权限类型：1-菜单、2-按钮、3-接口
4. 删除权限前需确保没有子权限
5. 树形结构 parentId 为 0 或 null 表示根节点
