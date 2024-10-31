package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.json_mapper.dto.PurchaseReturnDTO;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnDetail;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseReturnMapper {
    PurchaseReturnDTO toMasterDTO(PurchaseReturnMaster purchaseMaster);

    PurchaseReturnDTO.PurchaseReturnDetailDTO toDetailDTO(PurchaseReturnDetail purchaseDetail);
}
