package io.nomard.spoty_api_v1.repositories.quotations;

import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface QuotationMasterRepository extends PagingAndSortingRepository<QuotationMaster, Long>, JpaRepository<QuotationMaster, Long> {
    @Query("SELECT qm FROM QuotationMaster qm WHERE qm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(qm.ref)) LIKE %:search% AND (qm.approved = true OR qm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND a.level = qm.nextApprovedLevel) > 0)")
    ArrayList<QuotationMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select qm from QuotationMaster qm where qm.tenant.id = :tenantId AND (qm.approved = true OR qm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND a.level = qm.nextApprovedLevel) > 0)")
    Page<QuotationMaster> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}
