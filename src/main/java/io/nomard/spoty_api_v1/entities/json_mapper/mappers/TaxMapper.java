package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.DiscountDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.TaxDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaxMapper {
    TaxDTO toDTO(Tax tax);
}
