package io.nomard.spoty_api_v1.repositories.adjustments;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AdjustmentRepository extends ReactiveSortingRepository<AdjustmentMaster, Long>, ReactiveCrudRepository<AdjustmentMaster, Long> {
    @Query("SELECT p FROM AdjustmentMaster p WHERE p.tenant.id = :id AND TRIM(LOWER(p.ref)) LIKE %:ref")
    Flux<AdjustmentMaster> search(
            @Param("id") Long id,
            @Param("ref") String ref);

    @Query("SELECT p FROM AdjustmentMaster p WHERE p.tenant.id = :id")
    Flux<AdjustmentMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
