package io.nomard.spoty_api_v1.repositories.adjustments;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdjustmentMasterRepository extends PagingAndSortingRepository<AdjustmentMaster, Long>, JpaRepository<AdjustmentMaster, Long> {
    @Query("SELECT am FROM AdjustmentMaster am WHERE am.tenant.id = :tenantId " +
            "AND TRIM(LOWER(am.ref)) LIKE %:search% AND (am.approved = true OR am.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId) > 0)")
    List<AdjustmentMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("SELECT am FROM AdjustmentMaster am WHERE am.tenant.id = :tenantId AND (am.approved = true OR am.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId) > 0)")
    Page<AdjustmentMaster> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}
