package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.utils.json_mapper.dto.SaleReturnDTO;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnDetail;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleReturnMapper {
    SaleReturnDTO toMasterDTO(SaleReturnMaster saleReturnMaster);

    SaleReturnDTO.SaleReturnDetailDTO toDetailDTO(SaleReturnDetail saleReturnDetail);
}
