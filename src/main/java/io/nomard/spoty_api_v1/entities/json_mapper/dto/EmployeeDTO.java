package io.nomard.spoty_api_v1.entities.json_mapper.dto;

import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeDTO() {

    public record EmployeeAsWholeDTO(
            Long id,
            Branch branch,
            Department department,
            Designation designation,
            EmploymentStatus employmentStatus,
            User user,
            Role role,
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
            Employee createdBy,
            LocalDateTime updatedAt,
            Employee updatedBy) {
    }

    public record EmployeeAsAccessor(
            Branch branch,
            Department department,
            Designation designation,
            User user,
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
}