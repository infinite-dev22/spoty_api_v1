package io.nomard.spoty_api_v1.entities.json_mapper.dto;

public record UserDTO(
        Long id,
        String email,
        String password,
        String userType,
        boolean active,
        boolean locked) {
}