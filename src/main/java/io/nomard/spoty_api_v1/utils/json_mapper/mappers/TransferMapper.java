package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.utils.json_mapper.dto.TransferDTO;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {
    TransferDTO toMasterDTO(TransferMaster transferMaster);

    TransferDTO.TransferDetailDTO toDetailDTO(TransferDetail transferDetail);
}
