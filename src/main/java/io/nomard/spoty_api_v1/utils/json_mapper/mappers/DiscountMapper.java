package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.DiscountDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    DiscountDTO toDTO(Discount discount);
}
