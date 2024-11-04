package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.AdjustmentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdjustmentMapper {
    AdjustmentDTO toMasterDTO(AdjustmentMaster adjustmentMaster);

    AdjustmentDTO.AdjustmentDetailDTO toDetailDTO(AdjustmentDetail adjustmentDetail);
}
