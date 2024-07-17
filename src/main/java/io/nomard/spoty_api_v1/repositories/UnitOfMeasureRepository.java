package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UnitOfMeasureRepository extends ReactiveSortingRepository<UnitOfMeasure, Long>, ReactiveCrudRepository<UnitOfMeasure, Long> {
    @Query("SELECT uom " +
            "FROM UnitOfMeasure uom " +
            "WHERE uom.tenant.id = :id " +
            "AND CONCAT(LOWER(uom.name), LOWER(uom.shortName)) " +
            "LIKE %:search%")
    Flux<UnitOfMeasure> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM UnitOfMeasure p WHERE p.tenant.id = :id")
    Flux<UnitOfMeasure> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
