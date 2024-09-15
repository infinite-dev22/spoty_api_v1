package io.nomard.spoty_api_v1.repositories.requisitions;

import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RequisitionMasterRepository extends PagingAndSortingRepository<RequisitionMaster, Long>, JpaRepository<RequisitionMaster, Long> {
    @Query("SELECT rm FROM RequisitionMaster rm WHERE rm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(rm.ref)) LIKE %:search% AND (rm.approved = true OR rm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND a.level = rm.latestApprovedLevel) > 0)")
    ArrayList<RequisitionMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select rm from RequisitionMaster rm where rm.tenant.id = :tenantId AND (rm.approved = true OR rm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND a.level = rm.latestApprovedLevel) > 0)")
    Page<RequisitionMaster> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}
