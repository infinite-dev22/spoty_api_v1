package io.nomard.spoty_api_v1.entities.json_mapper.dto;

import io.nomard.spoty_api_v1.entities.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeDTO() {

    public record EmployeeAsWholeDTO(
            Long id,
            DepartmentDTO.DepartmentAsPartDTO department,
            DesignationDTO.DesignationAsPartDTO designation,
            EmploymentStatusDTO.EmploymentStatusAsPartDTO employmentStatus,
            UserDTO user,
            RoleDTO.RoleForName role,
            String firstName,
            String lastName,
            String otherName,
            String phone,
            String avatar,
            String email,
            String salary,
            LocalDate startDate,
            String employmentConfirmationLetter,
            boolean active,
            boolean locked,
            LocalDateTime createdAt,
            EmployeeAsEditorDTO createdBy,
            LocalDateTime updatedAt,
            EmployeeAsEditorDTO updatedBy) {
    }

    public record EmployeeAsAccessor(
            DepartmentDTO.DepartmentAsPartDTO department,
            DesignationDTO.DesignationAsPartDTO designation,
            EmploymentStatusDTO.EmploymentStatusAsPartDTO employmentStatus,
            UserDTO user,
            Role role,
            String firstName,
            String lastName,
            String otherName,
            String phone,
            String avatar,
            String email,
            String employmentConfirmationLetter,
            boolean active,
            boolean locked) {
    }

    public record EmployeeAsEditorDTO(
            String firstName,
            String lastName,
            String otherName) {
    }

    public record EmployeeAsReviewerDTO(
            String firstName,
            String lastName,
            String otherName,
            String avatar) {
    }
}