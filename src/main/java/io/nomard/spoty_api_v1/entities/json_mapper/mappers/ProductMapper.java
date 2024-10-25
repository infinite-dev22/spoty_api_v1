package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.BranchDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
}
