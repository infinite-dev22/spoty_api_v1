package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.EmploymentStatusDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmploymentStatusMapper {
    EmploymentStatusDTO.EmploymentStatusAsWholeDTO toWholeDTO(EmploymentStatus employmentStatus);
    EmploymentStatusDTO.EmploymentStatusAsPartDTO toPartDTO(EmploymentStatus employmentStatus);
}
