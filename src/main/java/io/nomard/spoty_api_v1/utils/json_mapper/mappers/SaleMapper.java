package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.utils.json_mapper.dto.SaleDTO;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    SaleDTO toMasterDTO(SaleMaster saleMaster);

    SaleDTO.SaleDetailDTO toDetailDTO(SaleDetail saleDetail);
}
