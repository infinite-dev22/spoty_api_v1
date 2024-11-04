package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.utils.json_mapper.dto.QuotationDTO;
import io.nomard.spoty_api_v1.entities.quotations.QuotationDetail;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuotationMapper {
    QuotationDTO toMasterDTO(QuotationMaster quotationMaster);

    QuotationDTO.QuotationDetailDTO toDetailDTO(QuotationDetail quotationDetail);
}
