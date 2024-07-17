package io.nomard.spoty_api_v1.repositories.hrm.leave;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LeaveRepository extends ReactiveSortingRepository<Leave, Long>, ReactiveCrudRepository<Leave, Long> {
    @Query("SELECT l " +
            "FROM Leave l " +
            "WHERE l.tenant.id = :id " +
            "AND CONCAT(LOWER(l.employee.userProfile.firstName)," +
            "LOWER(l.employee.userProfile.firstName)," +
            "LOWER(l.employee.userProfile.lastName)," +
            "LOWER(l.employee.userProfile.otherName)," +
            "LOWER(l.employee.userProfile.email)," +
            "LOWER(l.employee.role.name)," +
            "LOWER(l.employee.role.label)," +
            "LOWER(l.leaveType.name)," +
            "LOWER(l.leaveType.color)," +
            "LIKE %:search%")
    Flux<Leave> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT l FROM Leave l WHERE l.tenant.id = :id")
    Flux<Leave> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
