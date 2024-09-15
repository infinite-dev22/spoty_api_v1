package io.nomard.spoty_api_v1.repositories.stock_ins;

import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface StockInMasterRepository extends PagingAndSortingRepository<StockInMaster, Long>, JpaRepository<StockInMaster, Long> {
    @Query("SELECT sim FROM StockInMaster sim WHERE sim.tenant.id = :tenantId " +
            "AND TRIM(LOWER(sim.ref)) LIKE %:search% AND (sim.approved = true OR sim.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND a.level = sim.latestApprovedLevel) > 0)")
    ArrayList<StockInMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select sim from StockInMaster sim where sim.tenant.id = :tenantId AND (sim.approved = true OR sim.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND a.level = sim.latestApprovedLevel) > 0)")
    Page<StockInMaster> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}
