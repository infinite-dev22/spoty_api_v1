package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.json_mapper.dto.StockInDTO;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInDetail;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockInMapper {
    StockInDTO toMasterDTO(StockInMaster stockInMaster);

    StockInDTO.StockInDetailDTO toDetailDTO(StockInDetail stockInDetail);
}
