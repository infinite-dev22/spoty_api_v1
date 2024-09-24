package io.nomard.spoty_api_v1.repositories.purchases;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.LineChartModel;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseMasterRepository
    extends
        PagingAndSortingRepository<PurchaseMaster, Long>,
        JpaRepository<PurchaseMaster, Long> {
    @Query(
        "SELECT DATE_PART('year', CAST(pm.createdAt AS date)) AS period, SUM(pm.amountPaid) AS totalValue " +
        "FROM PurchaseMaster pm " +
        "WHERE pm.tenant.id = :id AND pm.approved = true " +
        "GROUP BY period " +
        "ORDER BY DATE_PART('year', CAST(pm.createdAt AS date))"
    )
    List<LineChartModel> yearlyExpenses(@Param("id") Long id);

    @Query(
        value = "SELECT TO_CHAR(TO_DATE(CONCAT(EXTRACT(YEAR FROM CURRENT_DATE), '-', months.month, '-01'), 'YYYY-MM-DD'), 'YYYY Month') AS period, COALESCE(SUM(pm.amount_paid), 0) AS totalValue " +
        "FROM (SELECT '01' AS month UNION ALL SELECT '02' UNION ALL SELECT '03' UNION ALL SELECT '04' UNION ALL " +
        "SELECT '05' UNION ALL SELECT '06' UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL " +
        "SELECT '09' UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL SELECT '12') AS months " +
        "LEFT JOIN purchase_master pm ON TO_CHAR(pm.created_at, 'YYYY-MM') = CONCAT(EXTRACT(YEAR FROM CURRENT_DATE), '-', months.month) " +
        "AND pm.tenant_id = :id  AND pm.approved = true " +
        "GROUP BY months.month " +
        "ORDER BY months.month::int",
        nativeQuery = true
    )
    List<LineChartModel> monthlyExpenses(@Param("id") Long id);

    @Query(
        "SELECT CAST(pm.createdAt AS date) AS period, SUM(pm.amountPaid) AS totalValue " +
        "FROM PurchaseMaster pm " +
        "WHERE pm.tenant.id = :id AND pm.approved = true " +
        "GROUP BY period " +
        "ORDER BY CAST(pm.createdAt AS date)"
    )
    List<LineChartModel> weeklyExpenses(@Param("id") Long id);

    @Query(
        "SELECT pm FROM PurchaseMaster pm WHERE pm.tenant.id = :tenantId " +
        "AND TRIM(LOWER(pm.ref)) LIKE %:search% AND (pm.approved = true OR pm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND pm.latestApprovedLevel = a.level - 1) > 0)"
    )
    ArrayList<PurchaseMaster> searchAll(
        @Param("tenantId") Long tenantId,
        @Param("search") String search
    );

    @Query(
        "SELECT pm FROM PurchaseMaster pm WHERE pm.tenant.id = :tenantId " +
        "AND (pm.approved = true OR pm.createdBy.id = :userId OR " +
        "(SELECT COUNT(a) FROM Approver a WHERE a.employee.id = :userId AND pm.latestApprovedLevel = a.level - 1) > 0)"
    )
    Page<PurchaseMaster> findAllByTenantId(
        @Param("tenantId") Long tenantId,
        @Param("userId") Long userId,
        Pageable pageable
    );

    @Query(
        "SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Purchases', SUM(pm.amountPaid)) " +
        "FROM PurchaseMaster pm " +
        "WHERE pm.tenant.id = :id AND pm.approved = true"
    )
    DashboardKPIModel totalPurchases(@Param("id") Long id);
}
