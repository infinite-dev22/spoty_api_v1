package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.ProductCategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    ProductCategoryDTO toDTO(ProductCategory productCategory);
}
