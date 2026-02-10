package com.example.ddd.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Permission;
import com.example.ddd.domain.repository.PermissionRepository;
import com.example.ddd.domain.service.PermissionDomainService;
import com.example.ddd.interfaces.rest.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限应用服务
 * 编排领域对象，处理用例逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionApplicationService extends ApplicationService {

    private final PermissionDomainService permissionDomainService;
    private final PermissionRepository permissionRepository;

    /**
     * 创建权限用例
     *
     * @param permission 权限对象
     * @return 创建的权限
     */
    @CacheEvict(value = "permissions", allEntries = true)
    public Permission createPermission(Permission permission) {
        beforeExecute();
        try {
            return permissionDomainService.createPermission(permission);
        } finally {
            afterExecute();
        }
    }

    /**
     * 更新权限用例
     *
     * @param permission 权限对象
     * @return 更新的权限
     */
    @CacheEvict(value = "permission", key = "#permission.id")
    public Permission updatePermission(Permission permission) {
        beforeExecute();
        try {
            return permissionDomainService.updatePermission(permission);
        } finally {
            afterExecute();
        }
    }

    /**
     * 删除权限用例
     *
     * @param id 权限ID
     */
    @CacheEvict(value = {"permission", "permissions"}, allEntries = true)
    public void deletePermission(Long id) {
        beforeExecute();
        try {
            permissionDomainService.deletePermission(id);
        } finally {
            afterExecute();
        }
    }

    /**
     * 查询权限用例
     *
     * @param id 权限ID
     * @return 权限对象
     */
    @Cacheable(value = "permission", key = "#id")
    public Permission getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return permission;
    }

    /**
     * 根据权限编码查询权限
     *
     * @param permissionCode 权限编码
     * @return 权限对象
     */
    @Cacheable(value = "permission", key = "'permissionCode:' + #permissionCode")
    public Permission getPermissionByPermissionCode(String permissionCode) {
        Permission permission = permissionRepository.findByPermissionCode(permissionCode);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return permission;
    }

    /**
     * 分页查询权限
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页结果
     */
    @Cacheable(value = "permissions", key = "'page:' + #current + ':' + #size")
    public IPage<Permission> pagePermissions(Long current, Long size) {
        return permissionRepository.page(new Page<>(current, size));
    }

    /**
     * 查询所有权限（树形结构）
     *
     * @return 权限树
     */
    @Cacheable(value = "permissions", key = "'tree'")
    public List<Permission> getPermissionTree() {
        return permissionDomainService.getPermissionTree();
    }

    /**
     * 根据父权限ID查询子权限
     *
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    @Cacheable(value = "permissions", key = "'parentId:' + #parentId")
    public List<Permission> getPermissionsByParentId(Long parentId) {
        return permissionRepository.findByParentId(parentId);
    }

    /**
     * 根据权限类型查询权限
     *
     * @param permissionType 权限类型
     * @return 权限列表
     */
    @Cacheable(value = "permissions", key = "'type:' + #permissionType")
    public List<Permission> getPermissionsByType(Integer permissionType) {
        return permissionRepository.findByPermissionType(permissionType);
    }
}
