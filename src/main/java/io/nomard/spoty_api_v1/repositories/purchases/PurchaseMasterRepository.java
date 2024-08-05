package io.nomard.spoty_api_v1.repositories.purchases;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.LineChartModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PurchaseMasterRepository extends PagingAndSortingRepository<PurchaseMaster, Long>, JpaRepository<PurchaseMaster, Long> {
    @Query("SELECT DATE_FORMAT(e.createdAt, '%Y') AS period, SUM(e.amountPaid) AS totalValue " +
            "FROM PurchaseMaster e " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(e.createdAt, '%Y')")
    List<LineChartModel> yearlyExpenses(@Param("id") Long id);

    @Query(value = "SELECT TO_CHAR(TO_DATE(CONCAT(EXTRACT(YEAR FROM CURRENT_DATE), '-', months.month, '-01'), 'YYYY-MM-DD'), 'YYYY Month') AS period, COALESCE(SUM(e.amount_paid), 0) AS totalValue " +
            "FROM (SELECT '01' AS month UNION ALL SELECT '02' UNION ALL SELECT '03' UNION ALL SELECT '04' UNION ALL " +
            "SELECT '05' UNION ALL SELECT '06' UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL " +
            "SELECT '09' UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL SELECT '12') AS months " +
            "LEFT JOIN purchase_master e ON TO_CHAR(e.created_at, 'YYYY-MM') = CONCAT(EXTRACT(YEAR FROM CURRENT_DATE), '-', months.month) " +
            "AND e.tenant_id = :id " +
            "GROUP BY months.month " +
            "ORDER BY months.month::int", nativeQuery = true)
    List<LineChartModel> monthlyExpenses(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(e.createdAt, '%Y-%m-%u') AS period, SUM(e.amountPaid) AS totalValue " +
            "FROM PurchaseMaster e " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(e.createdAt, '%Y-%m-%u')")
    List<LineChartModel> weeklyExpenses(@Param("id") Long id);

    @Query("SELECT pm FROM PurchaseMaster pm WHERE pm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(pm.ref)) LIKE %:search%")
    ArrayList<PurchaseMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from PurchaseMaster p where p.tenant.id = :id")
    Page<PurchaseMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Purchases', SUM(s.amountPaid)) " +
            "FROM PurchaseMaster s " +
            "WHERE s.tenant.id = :id ")
    DashboardKPIModel totalPurchases(@Param("id") Long id);
}
