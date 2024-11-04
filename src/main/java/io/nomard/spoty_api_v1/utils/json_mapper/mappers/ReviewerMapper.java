package io.nomard.spoty_api_v1.utils.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.ReviewerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewerMapper {
    ReviewerDTO toDTO(Reviewer reviewer);
}
