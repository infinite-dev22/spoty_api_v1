package io.nomard.spoty_api_v1.repositories.hrm.hrm;

import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EmploymentStatusRepository extends ReactiveSortingRepository<EmploymentStatus, Long>, ReactiveCrudRepository<EmploymentStatus, Long> {
    @Query("SELECT es " +
            "FROM EmploymentStatus es " +
            "WHERE es.tenant.id = :id " +
            "AND CONCAT(LOWER(es.name), LOWER(es.color)) " +
            "LIKE %:search%")
    Flux<EmploymentStatus> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM EmploymentStatus p WHERE p.tenant.id = :id")
    Flux<EmploymentStatus> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
