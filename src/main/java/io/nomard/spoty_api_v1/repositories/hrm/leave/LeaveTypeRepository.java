package io.nomard.spoty_api_v1.repositories.hrm.leave;

import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypeRepository extends PagingAndSortingRepository<LeaveType, Long>, JpaRepository<LeaveType, Long> {
    @Query("select p from LeaveType p where p.tenant.id = :id")
    Page<LeaveType> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
