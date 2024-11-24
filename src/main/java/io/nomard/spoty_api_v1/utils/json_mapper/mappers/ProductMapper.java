package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
}
