package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.EmployeeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeDTO.EmployeeAsWholeDTO toWholeDTO(Employee employee);

    EmployeeDTO.EmployeeAsAccessor toAccessorDTO(Employee employee);

    EmployeeDTO.EmployeeAsEditorDTO toEditorDTO(Employee employee);
}
