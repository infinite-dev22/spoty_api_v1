package io.nomard.spoty_api_v1.repositories.hrm.hrm;

import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DesignationRepository extends ReactiveSortingRepository<Designation, Long>, ReactiveCrudRepository<Designation, Long> {
    @Query("SELECT d " +
            "FROM Designation d " +
            "WHERE d.tenant.id = :id " +
            "AND CONCAT(LOWER(d.name), LOWER(d.designation)) " +
            "LIKE %:search%")
    Flux<Designation> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT d FROM Designation d WHERE d.tenant.id = :id")
    Flux<Designation> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
