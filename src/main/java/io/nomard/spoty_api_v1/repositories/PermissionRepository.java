package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long>, JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
