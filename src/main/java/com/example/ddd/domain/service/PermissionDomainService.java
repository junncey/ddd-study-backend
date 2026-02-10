package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Permission;
import com.example.ddd.domain.repository.PermissionRepository;
import com.example.ddd.interfaces.rest.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 权限领域服务
 * 包含核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionDomainService extends DomainService {

    private final PermissionRepository permissionRepository;

    /**
     * 创建权限
     * 包含业务规则验证
     *
     * @param permission 权限对象
     * @return 创建的权限
     */
    @Transactional(rollbackFor = Exception.class)
    public Permission createPermission(Permission permission) {
        // 业务规则验证
        validate();

        // 检查权限编码是否已存在
        if (permissionRepository.existsByPermissionCode(permission.getPermissionCode())) {
            throw new BusinessException("权限编码已存在");
        }

        // 如果有父权限，检查父权限是否存在
        if (permission.getParentId() != null && permission.getParentId() > 0) {
            Permission parentPermission = permissionRepository.findById(permission.getParentId());
            if (parentPermission == null) {
                throw new BusinessException("父权限不存在");
            }
        }

        // 保存权限
        permissionRepository.save(permission);

        // 发布领域事件
        publishEvent("PermissionCreated: " + permission.getId());

        return permission;
    }

    /**
     * 更新权限
     *
     * @param permission 权限对象
     * @return 更新的权限
     */
    @Transactional(rollbackFor = Exception.class)
    public Permission updatePermission(Permission permission) {
        // 检查权限是否存在
        Permission existingPermission = permissionRepository.findById(permission.getId());
        if (existingPermission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查权限编码是否被其他权限使用
        Permission permissionWithSameCode = permissionRepository.findByPermissionCode(permission.getPermissionCode());
        if (permissionWithSameCode != null && !permissionWithSameCode.getId().equals(permission.getId())) {
            throw new BusinessException("权限编码已被使用");
        }

        // 如果有父权限，检查父权限是否存在
        if (permission.getParentId() != null && permission.getParentId() > 0) {
            // 不能将自己设置为父权限
            if (permission.getParentId().equals(permission.getId())) {
                throw new BusinessException("不能将自己设置为父权限");
            }

            Permission parentPermission = permissionRepository.findById(permission.getParentId());
            if (parentPermission == null) {
                throw new BusinessException("父权限不存在");
            }
        }

        // 更新权限
        permissionRepository.update(permission);

        return permission;
    }

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        // 检查权限是否存在
        Permission existingPermission = permissionRepository.findById(id);
        if (existingPermission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查是否有子权限
        List<Permission> childPermissions = permissionRepository.findByParentId(id);
        if (!childPermissions.isEmpty()) {
            throw new BusinessException("存在子权限，无法删除");
        }

        // TODO: 检查权限是否已分配给角色

        // 删除权限
        permissionRepository.delete(id);

        // 发布领域事件
        publishEvent("PermissionDeleted: " + id);
    }

    /**
     * 获取权限树
     *
     * @return 权限树
     */
    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = permissionRepository.findAll();
        return buildTree(allPermissions, 0L);
    }

    /**
     * 构建权限树
     *
     * @param permissions 权限列表
     * @param parentId   父权限ID
     * @return 树形结构
     */
    private List<Permission> buildTree(List<Permission> permissions, Long parentId) {
        return permissions.stream()
                .filter(permission -> {
                    Long pid = permission.getParentId();
                    if (parentId == 0L) {
                        return pid == null || pid == 0;
                    }
                    return parentId.equals(pid);
                })
                .peek(permission -> {
                    List<Permission> children = buildTree(permissions, permission.getId());
                    // 这里可以设置 children 属性，如果实体中有该字段
                })
                .toList();
    }
}
