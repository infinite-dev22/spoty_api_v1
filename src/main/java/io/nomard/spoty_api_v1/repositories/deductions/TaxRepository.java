package io.nomard.spoty_api_v1.repositories.deductions;

import io.nomard.spoty_api_v1.entities.deductions.Tax;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TaxRepository extends ReactiveSortingRepository<Tax, Long>, ReactiveCrudRepository<Tax, Long> {
    @Query("SELECT p FROM Tax p WHERE p.tenant.id = :id")
    Flux<Tax> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
