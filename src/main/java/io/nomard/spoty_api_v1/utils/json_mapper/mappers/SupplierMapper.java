package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.SupplierDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDTO toDTO(Supplier supplier);
}
