package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.AccountTransactionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountTransactionMapper {
    AccountTransactionDTO toDTO(AccountTransaction accountTransaction);
}
