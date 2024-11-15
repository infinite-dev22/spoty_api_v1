package io.nomard.spoty_api_v1.repositories.sales;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.LineChartModel;
import io.nomard.spoty_api_v1.models.ProductSalesModel;
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
public interface SaleMasterRepository extends PagingAndSortingRepository<SaleMaster, Long>, JpaRepository<SaleMaster, Long> {
    @Query("SELECT DATE_PART('year', CAST(sm.createdAt AS date)) AS period, SUM(sm.amountPaid) AS totalValue " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND sm.approved = true " +
            "GROUP BY period " +
            "ORDER BY DATE_PART('year', CAST(sm.createdAt AS date))")
    List<LineChartModel> yearlyIncomes(@Param("id") Long id);

    @Query(value = "SELECT TO_CHAR(TO_DATE(CONCAT(EXTRACT(YEAR FROM CURRENT_DATE), '-', months.month, '-01'), 'YYYY-MM-DD'), 'YYYY Month') AS period, COALESCE(SUM(sm.amount_paid), 0) AS totalValue " +
            "FROM (SELECT '01' AS month UNION ALL SELECT '02' UNION ALL SELECT '03' UNION ALL SELECT '04' UNION ALL " +
            "SELECT '05' UNION ALL SELECT '06' UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL " +
            "SELECT '09' UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL SELECT '12') AS months " +
            "LEFT JOIN sale_master sm ON TO_CHAR(sm.created_at, 'YYYY-MM') = CONCAT(EXTRACT(YEAR FROM CURRENT_DATE), '-', months.month) " +
            "AND sm.tenant_id = :id AND sm.approved = true " +
            "GROUP BY months.month " +
            "ORDER BY months.month::int", nativeQuery = true)
    List<LineChartModel> monthlyIncomes(@Param("id") Long id);

    @Query("SELECT CAST(sm.createdAt AS date) AS period, SUM(sm.amountPaid) AS totalValue " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND sm.approved = true " +
            "GROUP BY period " +
            "ORDER BY CAST(sm.createdAt AS date)")
    List<LineChartModel> weeklyIncomes(@Param("id") Long id);

    @Query("SELECT DATE_PART('year', CAST(sm.createdAt AS date)) AS period, SUM(sm.amountPaid) AS totalValue " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND sm.approved = true " +
            "GROUP BY period " +
            "ORDER BY DATE_PART('year', CAST(sm.createdAt AS date))")
    List<LineChartModel> yearlyRevenue(@Param("id") Long id);

    @Query("SELECT DATE_PART('month', CAST(sm.createdAt AS date)) AS period, SUM(sm.amountPaid) AS totalValue " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND sm.approved = true " +
            "GROUP BY period " +
            "ORDER BY DATE_PART('month', CAST(sm.createdAt AS date))")
    List<LineChartModel> monthlyRevenue(@Param("id") Long id);

    @Query("SELECT CAST(sm.createdAt AS date) AS period, SUM(sm.amountPaid) AS totalValue " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND sm.approved = true " +
            "GROUP BY period " +
            "ORDER BY CAST(sm.createdAt AS date)")
    List<LineChartModel> weeklyRevenue(@Param("id") Long id);

    @Query("SELECT new io.nomard.spoty_api_v1.models.ProductSalesModel(p.name, SUM(sd.quantity), p.salePrice, p.costPrice) " +
            "FROM SaleDetail sd " +
            "JOIN sd.product p " +
            "JOIN sd.sale s " +
            "WHERE s.tenant.id = :id AND s.approved = true " +
            "GROUP BY p.id, p.name, p.salePrice, p.costPrice " +
            "ORDER BY SUM(sd.quantity) DESC")
    List<ProductSalesModel> findTopProductsSold(@Param("id") Long id, Pageable pageable);

//    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Orders', COUNT(s)) " +
//            "FROM SaleMaster s " +
//            "WHERE s.tenant.id = :id")
//    DashboardKPIModel countOrders(@Param("id") Long id);

    @Query("SELECT SUM(sm.amountPaid) " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id " +
            "AND sm.approved = true " +
            "AND TO_CHAR(CAST(sm.createdAt AS date), 'YYYY-MM') = TO_CHAR(CAST(CURRENT_DATE AS date), 'YYYY-MM')"
    )
    Number revenue(@Param("id") Long id);

    @Query(
            "SELECT COUNT(sm) " +
                    "FROM SaleMaster sm " +
                    "WHERE sm.tenant.id = :id " +
                    "AND sm.approved = true " +
                    "AND TO_CHAR(CAST(sm.createdAt AS date), 'YYYY-MM') = TO_CHAR(CAST(CURRENT_DATE AS date), 'YYYY-MM')"
    )
    Number numberOfSales(@Param("id") Long id);

    @Query("SELECT sm " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id " +
            "AND sm.approved = true " +
            "AND TO_CHAR(CAST(sm.createdAt AS date), 'YYYY-MM') = TO_CHAR(CAST(CURRENT_DATE AS date), 'YYYY-MM')"
    )
    ArrayList<SaleMaster> salesForCost(@Param("id") Long id);

    @Query("SELECT sm FROM SaleMaster sm WHERE sm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(sm.ref)) LIKE %:search% AND (sm.approved = true OR sm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId AND a.level = sm.nextApprovedLevel) > 0)")
    ArrayList<SaleMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("SELECT sm FROM SaleMaster sm WHERE sm.tenant.id = :tenantId AND (sm.approved = true OR sm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId AND a.level = sm.nextApprovedLevel) > 0)")
    Page<SaleMaster> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}
