package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.accounting.Expense;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.ExpenseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    ExpenseDTO toDTO(Expense expense);
}
