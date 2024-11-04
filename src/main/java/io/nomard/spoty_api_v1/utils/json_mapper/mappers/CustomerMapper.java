package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
}
