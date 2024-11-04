package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.UnitOfMeasureDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnitOfMeasureMapper {
    UnitOfMeasureDTO.AsWholeDTO toDTO(UnitOfMeasure unitOfMeasure);
}
