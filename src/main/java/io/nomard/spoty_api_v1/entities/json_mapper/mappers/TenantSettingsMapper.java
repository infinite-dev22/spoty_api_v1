package io.nomard.spoty_api_v1.entities.json_mapper.mappers;

import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.TenantSettings;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.ReviewerDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.TenantSettingsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantSettingsMapper {
    TenantSettingsDTO toDTO(TenantSettings tenantSettings);
}