package io.nomard.spoty_api_v1.repositories.purchases;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
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
    List<LineChartModel> yearlyPurchases(@Param("id") Long id);

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
    List<LineChartModel> monthlyPurchases(@Param("id") Long id);

    @Query(
            "SELECT CAST(pm.createdAt AS date) AS period, SUM(pm.amountPaid) AS totalValue " +
                    "FROM PurchaseMaster pm " +
                    "WHERE pm.tenant.id = :id AND pm.approved = true " +
                    "GROUP BY period " +
                    "ORDER BY CAST(pm.createdAt AS date)"
    )
    List<LineChartModel> weeklyPurchases(@Param("id") Long id);

    @Query(
            "SELECT pm FROM PurchaseMaster pm WHERE pm.tenant.id = :tenantId " +
                    "AND TRIM(LOWER(pm.ref)) LIKE %:search% AND (pm.approved = true OR pm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId AND a.level = pm.nextApprovedLevel) > 0)"
    )
    ArrayList<PurchaseMaster> searchAll(
            @Param("tenantId") Long tenantId,
            @Param("userId") Long userId,
            @Param("search") String search
    );

    @Query(
            "SELECT pm FROM PurchaseMaster pm WHERE pm.tenant.id = :tenantId " +
                    "AND (pm.approved = true OR pm.createdBy.id = :userId OR " +
                    "(SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId AND a.level = pm.nextApprovedLevel) > 0)"
    )
    Page<PurchaseMaster> findAllByTenantId(
            @Param("tenantId") Long tenantId,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query(
            "SELECT SUM(pm.amountPaid) " +
                    "FROM PurchaseMaster pm " +
                    "WHERE pm.tenant.id = :id " +
                    "AND pm.approved = true " +
                    "AND TO_CHAR(CAST(pm.createdAt AS date), 'YYYY-MM') = TO_CHAR(CAST(CURRENT_DATE AS date), 'YYYY-MM')"
    )
    Number purchaseCost(@Param("id") Long id);

    @Query(
            "SELECT COUNT(pm) " +
                    "FROM PurchaseMaster pm " +
                    "WHERE pm.tenant.id = :id " +
                    "AND pm.approved = true " +
                    "AND TO_CHAR(CAST(pm.createdAt AS date), 'YYYY-MM') = TO_CHAR(CAST(CURRENT_DATE AS date), 'YYYY-MM')"
    )
    Number numberOfPurchases(@Param("id") Long id);

    @Query(
            "SELECT COUNT(pm) " +
                    "FROM PurchaseMaster pm " +
                    "WHERE pm.tenant.id = :id " +
                    "AND LOWER(pm.purchaseStatus) LIKE LOWER('cancelled') " +
                    "AND pm.approved = true " +
                    "AND TO_CHAR(CAST(pm.updatedAt AS date), 'YYYY-MM') = TO_CHAR(CAST(CURRENT_DATE AS date), 'YYYY-MM')"
    )
    Number totalCancelledPurchases(@Param("id") Long id);
}
