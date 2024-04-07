package org.hrsys.service;

import org.hrsys.exception.InvalidRequestException;
import org.hrsys.exception.ResourceNotFoundException;
import org.hrsys.model.Role;
import org.hrsys.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Role createRole(Role role) {
        validateRole(role); // Validate the role
        // Check if the role name is already in use
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new InvalidRequestException("The specified role name is already in use.");
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Role role) {
        validateRole(role); // Validate the role before processing
        Role existingRole = roleRepository.findById(role.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", role.getId()));
        // Check if the provided version matches the current version (optimistic locking)
        if (!Objects.equals(role.getVersion(), existingRole.getVersion())) {
            throw new InvalidRequestException("The provided version does not match the current version");
        }
        existingRole.setName(role.getName()); // Update the role name
        return roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        validateRoleId(id); // Validate the role ID
        roleRepository.deleteById(id);
    }

    // Method to validate a role
    private void validateRole(Role role) {
        if (role == null || role.getName() == null) {
            throw new InvalidRequestException("Role name must not be null");
        }
    }

    // Method to validate a role id
    private void validateRoleId(Long id) {
        if (id == null || !roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }
    }
}