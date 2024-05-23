package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends PagingAndSortingRepository<Tenant, Long>, JpaRepository<Tenant, Long> {
}
