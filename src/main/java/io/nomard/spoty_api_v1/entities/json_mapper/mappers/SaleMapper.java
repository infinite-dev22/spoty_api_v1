package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.SaleDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    SaleDTO toMasterDTO(SaleMaster saleMaster);

    SaleDTO.SaleDetailDTO toDetailDTO(SaleDetail saleDetail);
}
