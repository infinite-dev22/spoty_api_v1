package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Permission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends ReactiveSortingRepository<Permission, Long>, ReactiveCrudRepository<Permission, Long> {
}
