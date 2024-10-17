package io.nomard.spoty_api_v1.repositories.returns.sale_returns;

import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SaleReturnMasterRepository extends PagingAndSortingRepository<SaleReturnMaster, Long>, JpaRepository<SaleReturnMaster, Long> {
    @Query("SELECT srm FROM SaleReturnMaster srm WHERE srm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(srm.ref)) LIKE %:search% AND (srm.approved = true OR srm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId AND a.level = srm.nextApprovedLevel) > 0)")
    ArrayList<SaleReturnMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select srm from SaleReturnMaster srm where srm.tenant.id = :tenantId AND (srm.approved = true OR srm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId AND a.level = srm.nextApprovedLevel) > 0)")
    Page<SaleReturnMaster> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}
