package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.TenantSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantSettingsRepository extends JpaRepository<TenantSettings, Long> {
    @Query("select p from TenantSettings p where p.tenant.id = :id")
    TenantSettings findByTenantId(@Param("id") Long id);
}
