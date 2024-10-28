package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.json_mapper.dto.PurchaseDTO;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    PurchaseDTO toMasterDTO(PurchaseMaster purchaseMaster);

    PurchaseDTO.PurchaseDetailDTO toDetailDTO(PurchaseDetail purchaseDetail);
}
