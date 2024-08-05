package io.nomard.spoty_api_v1.repositories.sales;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
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
    @Query("SELECT DATE_FORMAT(e.createdAt, '%Y') AS period, SUM(e.amountPaid) AS totalValue " +
            "FROM SaleMaster e " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(e.createdAt, '%Y')")
    List<LineChartModel> yearlyIncomes(@Param("id") Long id);

    @Query(value = "SELECT TO_CHAR(TO_DATE(CONCAT(EXTRACT(YEAR FROM CURRENT_DATE), '-', months.month, '-01'), 'YYYY-MM-DD'), 'YYYY Month') AS period, COALESCE(SUM(i.amount_received), 0) AS totalValue " +
            "FROM (SELECT '01' AS month UNION ALL SELECT '02' UNION ALL SELECT '03' UNION ALL SELECT '04' UNION ALL " +
            "SELECT '05' UNION ALL SELECT '06' UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL " +
            "SELECT '09' UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL SELECT '12') AS months " +
            "LEFT JOIN income_master i ON TO_CHAR(i.created_at, 'YYYY-MM') = CONCAT(EXTRACT(YEAR FROM CURRENT_DATE), '-', months.month) " +
            "AND i.tenant_id = :id " +
            "GROUP BY months.month " +
            "ORDER BY months.month::int", nativeQuery = true)
    List<LineChartModel> monthlyIncomes(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(e.createdAt, '%M') AS period, SUM(e.amountPaid) AS totalValue " +
            "FROM SaleMaster e " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(e.createdAt, '%M')")
    List<LineChartModel> monthlyRevenue(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(e.createdAt, '%a') AS period, SUM(e.amountPaid) AS totalValue " +
            "FROM SaleMaster e " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(e.createdAt, '%a')")
    List<LineChartModel> weeklyRevenue(@Param("id") Long id);

    @Query("SELECT new io.nomard.spoty_api_v1.models.ProductSalesModel(p.name, SUM(sd.quantity), p.salePrice, p.costPrice) " +
            "FROM SaleDetail sd " +
            "JOIN sd.product p " +
            "JOIN sd.sale s " +
            "WHERE s.tenant.id = :id " +
            "GROUP BY p.id, p.name, p.salePrice, p.costPrice " +
            "ORDER BY SUM(sd.quantity) DESC")
    List<ProductSalesModel> findTopProductsSold(@Param("id") Long id, Pageable pageable);

//    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Orders', COUNT(s)) " +
//            "FROM SaleMaster s " +
//            "WHERE s.tenant.id = :id")
//    DashboardKPIModel countOrders(@Param("id") Long id);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Earnings', SUM(s.amountPaid)) " +
            "FROM SaleMaster s " +
            "WHERE s.tenant.id = :id ")
    DashboardKPIModel totalEarnings(@Param("id") Long id);

    @Query("SELECT sm FROM SaleMaster sm WHERE sm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(sm.ref)) LIKE %:search%")
    ArrayList<SaleMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from SaleMaster p where p.tenant.id = :id")
    Page<SaleMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
