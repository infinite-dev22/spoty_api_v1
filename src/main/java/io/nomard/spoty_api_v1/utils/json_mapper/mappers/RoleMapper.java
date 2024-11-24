package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.RoleDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO.RoleAsWholeDTO toDTO(Role role);
}
