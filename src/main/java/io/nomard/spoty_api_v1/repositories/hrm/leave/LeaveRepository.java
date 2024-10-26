package io.nomard.spoty_api_v1.repositories.hrm.leave;

import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends PagingAndSortingRepository<Leave, Long>, JpaRepository<Leave, Long> {
    @Query("select l from Leave l where l.tenant.id = :tenantId AND (l.createdBy.id = :userId OR (SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId) > 0)")
    Page<Leave> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}
