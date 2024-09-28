package io.nomard.spoty_api_v1.repositories.returns.purchase_returns;

import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PurchaseReturnMasterRepository extends PagingAndSortingRepository<PurchaseReturnMaster, Long>, JpaRepository<PurchaseReturnMaster, Long> {
    @Query("SELECT prm FROM PurchaseReturnMaster prm WHERE prm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(prm.ref)) LIKE %:search% AND (prm.approved = true OR prm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND a.level = prm.nextApprovedLevel) > 0)")
    ArrayList<PurchaseReturnMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select prm from PurchaseReturnMaster prm where prm.tenant.id = :tenantId AND (prm.approved = true OR prm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND a.level = prm.nextApprovedLevel) > 0)")
    Page<PurchaseReturnMaster> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}
