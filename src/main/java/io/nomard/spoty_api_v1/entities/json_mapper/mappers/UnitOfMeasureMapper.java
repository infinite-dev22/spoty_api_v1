package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.TaxDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.UnitOfMeasureDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnitOfMeasureMapper {
    UnitOfMeasureDTO toDTO(UnitOfMeasure unitOfMeasure);
}
