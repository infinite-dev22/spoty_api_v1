package io.nomard.spoty_api_v1.utils.json_mapper.dto;

import io.nomard.spoty_api_v1.entities.Permission;

import java.time.LocalDateTime;
import java.util.Set;

public record RoleDTO() {

    public record RoleAsWholeDTO(
            Long id,
            String name,
            String label,
            String description,
            Set<Permission> permissions,
            LocalDateTime createdAt,
            EmployeeDTO.EmployeeAsEditorDTO createdBy,
            LocalDateTime updatedAt,
            EmployeeDTO.EmployeeAsEditorDTO updatedBy) {
    }

    public record RoleForName(Long id, String name) {
    }
}