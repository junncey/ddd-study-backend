package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Role;
import com.example.ddd.domain.repository.RoleRepository;
import com.example.ddd.domain.repository.UserRoleRepository;
import com.example.ddd.interfaces.rest.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色领域服务
 * 包含核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleDomainService extends DomainService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * 创建角色
     * 包含业务规则验证
     *
     * @param role 角色对象
     * @return 创建的角色
     */
    @Transactional(rollbackFor = Exception.class)
    public Role createRole(Role role) {
        // 业务规则验证
        validate();

        // 检查角色编码是否已存在
        if (roleRepository.existsByRoleCode(role.getRoleCode())) {
            throw new BusinessException("角色编码已存在");
        }

        // 保存角色
        roleRepository.save(role);

        // 发布领域事件
        publishEvent("RoleCreated: " + role.getId());

        return role;
    }

    /**
     * 更新角色
     *
     * @param role 角色对象
     * @return 更新的角色
     */
    @Transactional(rollbackFor = Exception.class)
    public Role updateRole(Role role) {
        // 检查角色是否存在
        Role existingRole = roleRepository.findById(role.getId());
        if (existingRole == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色编码是否被其他角色使用
        Role roleWithSameCode = roleRepository.findByRoleCode(role.getRoleCode());
        if (roleWithSameCode != null && !roleWithSameCode.getId().equals(role.getId())) {
            throw new BusinessException("角色编码已被使用");
        }

        // 更新角色
        roleRepository.update(role);

        return role;
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 检查角色是否存在
        Role existingRole = roleRepository.findById(id);
        if (existingRole == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色是否已分配给用户
        if (userRoleRepository.existsByRoleId(id)) {
            throw new BusinessException("该角色已分配给用户，无法删除");
        }

        // 删除角色
        roleRepository.delete(id);

        // 发布领域事件
        publishEvent("RoleDeleted: " + id);
    }
}
