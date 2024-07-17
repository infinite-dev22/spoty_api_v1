package io.nomard.spoty_api_v1.repositories.hrm.leave;

import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LeaveTypeRepository extends ReactiveSortingRepository<LeaveType, Long>, ReactiveCrudRepository<LeaveType, Long> {
    @Query("SELECT p FROM LeaveType p WHERE p.tenant.id = :id")
    Flux<LeaveType> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
