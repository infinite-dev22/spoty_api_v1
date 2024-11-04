package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.PaymentTransactionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {
    PaymentTransactionDTO toDTO(PaymentTransaction paymentTransaction);
}
