package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.DesignationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DesignationMapper {
    DesignationDTO.DesignationAsWholeDTO toDTO(Designation designation);
}
