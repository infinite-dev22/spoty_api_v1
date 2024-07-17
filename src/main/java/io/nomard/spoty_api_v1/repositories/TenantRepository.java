package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Tenant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends ReactiveSortingRepository<Tenant, Long>, ReactiveCrudRepository<Tenant, Long> {
}
