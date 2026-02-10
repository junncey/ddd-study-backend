package com.example.ddd.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Role;
import com.example.ddd.domain.repository.RoleRepository;
import com.example.ddd.domain.service.RoleDomainService;
import com.example.ddd.interfaces.rest.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 角色应用服务
 * 编排领域对象，处理用例逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleApplicationService extends ApplicationService {

    private final RoleDomainService roleDomainService;
    private final RoleRepository roleRepository;

    /**
     * 创建角色用例
     *
     * @param role 角色对象
     * @return 创建的角色
     */
    @CacheEvict(value = "roles", allEntries = true)
    public Role createRole(Role role) {
        beforeExecute();
        try {
            return roleDomainService.createRole(role);
        } finally {
            afterExecute();
        }
    }

    /**
     * 更新角色用例
     *
     * @param role 角色对象
     * @return 更新的角色
     */
    @CacheEvict(value = "role", key = "#role.id")
    public Role updateRole(Role role) {
        beforeExecute();
        try {
            return roleDomainService.updateRole(role);
        } finally {
            afterExecute();
        }
    }

    /**
     * 删除角色用例
     *
     * @param id 角色ID
     */
    @CacheEvict(value = {"role", "roles"}, allEntries = true)
    public void deleteRole(Long id) {
        beforeExecute();
        try {
            roleDomainService.deleteRole(id);
        } finally {
            afterExecute();
        }
    }

    /**
     * 查询角色用例
     *
     * @param id 角色ID
     * @return 角色对象
     */
    @Cacheable(value = "role", key = "#id")
    public Role getRoleById(Long id) {
        Role role = roleRepository.findById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色对象
     */
    @Cacheable(value = "role", key = "'roleCode:' + #roleCode")
    public Role getRoleByRoleCode(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    /**
     * 分页查询角色
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页结果
     */
    @Cacheable(value = "roles", key = "'page:' + #current + ':' + #size")
    public IPage<Role> pageRoles(Long current, Long size) {
        return roleRepository.page(new Page<>(current, size));
    }
}
