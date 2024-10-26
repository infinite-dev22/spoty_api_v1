package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.DepartmentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentDTO.DepartmentAsWholeDTO toDTO(Department department);
}
