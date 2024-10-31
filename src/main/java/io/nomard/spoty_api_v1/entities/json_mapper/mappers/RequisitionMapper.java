package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.json_mapper.dto.RequisitionDTO;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionDetail;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequisitionMapper {
    RequisitionDTO toMasterDTO(RequisitionMaster requisitionMaster);

    RequisitionDTO.RequisitionDetailDTO toDetailDTO(RequisitionDetail requisitionDetail);
}
