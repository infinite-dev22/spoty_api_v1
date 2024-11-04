package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.BranchDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    BranchDTO toDTO(Branch branch);
}
