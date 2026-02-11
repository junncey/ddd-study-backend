# TODO 完善实现日志

## 日期
2025-02-11

## 概述
完善项目中的TODO项，实现完整的角色权限管理和Spring Security集成。

## 完成的任务

### 1. 创建用户角色关联实体和仓储

#### 新建文件
- `UserRole.java` - 用户角色关联实体（已存在，完善注解）
- `UserRoleRepository.java` - 用户角色关联仓储接口
- `UserRoleRepositoryImpl.java` - 用户角色关联仓储实现
- `UserRoleMapper.java` - 用户角色关联Mapper

#### 功能实现
- 添加`@TableId`、`@TableField`等MyBatis Plus注解
- 实现基础的CRUD操作
- 添加`findByRoleId`方法 - 根据角色ID查询关联记录
- 添加`findByUserId`方法 - 根据用户ID查询关联记录
- 添加`existsByRoleId`方法 - 检查角色是否已分配给用户
- 添加`findRoleCodesByUserId`方法 - 查询用户的角色编码集合
- 添加`findPermissionCodesByUserId`方法 - 查询用户的权限编码集合（通过角色）

### 2. 创建角色权限关联实体和仓储

#### 新建文件
- `RolePermission.java` - 角色权限关联实体（已存在，完善注解）
- `RolePermissionRepository.java` - 角色权限关联仓储接口
- `RolePermissionRepositoryImpl.java` - 角色权限关联仓储实现
- `RolePermissionMapper.java` - 角色权限关联Mapper

#### 功能实现
- 添加`@TableId`、`@TableField`等MyBatis Plus注解
- 实现基础的CRUD操作
- 添加`findByRoleId`方法 - 根据角色ID查询关联记录
- 添加`findByPermissionId`方法 - 根据权限ID查询关联记录
- 添加`existsByPermissionId`方法 - 检查权限是否已分配给角色
- 添加`findByRoleIdAndPermissionId`方法 - 根据角色ID和权限ID查询关联记录

### 3. 完善角色删除逻辑

#### 修改文件
- `RoleDomainService.java`

#### 实现内容
- 注入`UserRoleRepository`依赖
- 在`deleteRole`方法中添加角色分配检查
- 如果角色已分配给用户，抛出业务异常，阻止删除

```java
// 检查角色是否已分配给用户
if (userRoleRepository.existsByRoleId(id)) {
    throw new BusinessException("该角色已分配给用户，无法删除");
}
```

### 4. 完善权限删除逻辑

#### 修改文件
- `PermissionDomainService.java`

#### 实现内容
- 注入`RolePermissionRepository`依赖
- 在`deletePermission`方法中添加权限分配检查
- 如果权限已分配给角色，抛出业务异常，阻止删除

```java
// 检查权限是否已分配给角色
if (rolePermissionRepository.existsByPermissionId(id)) {
    throw new BusinessException("该权限已分配给角色，无法删除");
}
```

### 5. 实现Spring Security权限加载

#### 修改文件
- `UserDetailsServiceImpl.java`
- `UserDetailsImpl.java`

#### UserDetailsServiceImpl实现
- 注入`UserRoleRepository`依赖
- 在`loadUserByUsername`方法中加载用户的角色和权限
- 合并角色编码和权限编码作为Spring Security的权限集合

```java
// 加载用户的角色和权限
Set<String> roleCodes = userRoleRepository.findRoleCodesByUserId(user.getId());
Set<String> permissionCodes = userRoleRepository.findPermissionCodesByUserId(user.getId());

// 合并角色和权限
Set<String> authorities = new java.util.HashSet<>();
authorities.addAll(roleCodes);
authorities.addAll(permissionCodes);
```

#### UserDetailsImpl实现
- 重命名`create(User, Set<String>)`为`createWithAuthorities`避免方法签名冲突
- 添加权限编码到`GrantedAuthority`的转换逻辑

```java
public static UserDetailsImpl createWithAuthorities(User user, Set<String> authorityCodes) {
    Set<GrantedAuthority> authorities = authorityCodes.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    // ...
}
```

### 6. 实现从安全上下文获取当前用户

#### 修改文件
- `MybatisPlusConfig.java`

#### 实现内容
- 导入`SecurityContextHolder`和`Authentication`类
- 在`getCurrentUser`方法中从Spring Security安全上下文获取当前登录用户

```java
private String getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
        if (authentication.getPrincipal() instanceof String) {
            return "system";
        }
        return authentication.getName();
    }
    return "system";
}
```

## 数据库依赖

新实现的功能依赖以下数据库表：
- `t_user_role` - 用户角色关联表
- `t_role_permission` - 角色权限关联表

## 架构设计

### 六边形架构端口-适配器模式
- 领域层定义仓储接口（Port）
- 基础设施层实现仓储接口（Adapter）

### 权限加载策略
- 使用自定义SQL查询直接从数据库获取用户的角色和权限编码
- 通过连接三张表（t_user_role、t_role_permission、t_permission）获取用户权限
- 返回的权限集合包含角色编码和权限编码，用于Spring Security的权限控制

## 业务规则

1. **角色删除保护**：已分配给用户的角色无法删除，需先解除分配关系
2. **权限删除保护**：已分配给角色的权限无法删除，需先解除分配关系
3. **权限继承**：用户通过角色间接获得权限，权限加载时自动合并

## 测试建议

1. 测试删除已分配的角色，验证是否正确阻止
2. 测试删除已分配的权限，验证是否正确阻止
3. 测试用户登录后是否正确加载角色和权限
4. 测试自动填充的createBy和updateBy字段是否正确记录当前用户
5. 测试权限控制，验证用户是否能正确访问其拥有权限的接口

## 备注

- 所有关联表实体使用`@Builder`注解支持建造者模式
- 使用`@Serial`注解标注serialVersionUID字段
- Mapper中的自定义SQL查询使用`@Select`注解实现
- 权限编码查询中过滤了已删除和禁用的权限
