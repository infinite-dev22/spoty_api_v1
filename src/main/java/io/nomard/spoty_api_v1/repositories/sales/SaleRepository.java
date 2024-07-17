package io.nomard.spoty_api_v1.repositories.sales;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.LineChartModel;
import io.nomard.spoty_api_v1.models.ProductSalesModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SaleRepository extends ReactiveSortingRepository<SaleMaster, Long>, ReactiveCrudRepository<SaleMaster, Long> {
    @Query("SELECT DATE_FORMAT(sm.createdAt, '%Y') AS period, SUM(sm.amountPaid) AS totalValue " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(sm.createdAt, '%Y')")
    Flux<LineChartModel> yearlyIncomes(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(CONCAT(YEAR(CURDATE()), '-', months.month, '-01'), '%Y %M') AS period, " +
            "COALESCE(SUM(sm.amountPaid), 0) AS totalValue " +
            "FROM ( " +
            "    SELECT '01' AS month UNION ALL SELECT '02' UNION ALL SELECT '03' UNION ALL SELECT '04' UNION ALL " +
            "    SELECT '05' UNION ALL SELECT '06' UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL " +
            "    SELECT '09' UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL SELECT '12' " +
            ") AS months " +
            "LEFT JOIN SaleMaster sm ON DATE_FORMAT(sm.createdAt, '%Y-%m') = DATE_FORMAT(CONCAT(YEAR(CURDATE()), '-', months.month, '-01'), '%Y-%m') " +
            "AND sm.tenant.id = :id " +
            "GROUP BY months.month " +
            "ORDER BY FIELD(DATE_FORMAT(CONCAT(YEAR(CURDATE()), '-', months.month, '-01'), '%M'), " +
            "'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December') " +
            "LIMIT 12")
    Flux<LineChartModel> monthlyIncomes(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(sm.createdAt, '%M') AS period, SUM(sm.amountPaid) AS totalValue " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(sm.createdAt, '%M')")
    Flux<LineChartModel> monthlyRevenue(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(sm.createdAt, '%a') AS period, SUM(sm.amountPaid) AS totalValue " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(sm.createdAt, '%a')")
    Flux<LineChartModel> weeklyRevenue(@Param("id") Long id);

    @Query("SELECT new io.nomard.spoty_api_v1.models.ProductSalesModel(p.name, SUM(sd.quantity), p.salePrice, p.costPrice) " +
            "FROM SaleDetail sd " +
            "JOIN sd.product p " +
            "JOIN sd.sale sm " +
            "WHERE sm.tenant.id = :id " +
            "GROUP BY p.id, p.name, p.salePrice, p.costPrice " +
            "ORDER BY SUM(sd.quantity) DESC")
    Flux<ProductSalesModel> findTopProductsSold(@Param("id") Long id, Pageable pageable);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Earnings', SUM(sm.amountPaid)) " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id ")
    Mono<DashboardKPIModel> totalEarnings(@Param("id") Long id);

    @Query("SELECT sm " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id " +
            "AND CONCAT(LOWER(sm.ref)," +
            "LOWER(sm.customer.name)," +
            "LOWER(sm.customer.email)," +
            "LOWER(sm.customer.phone)," +
            "LOWER(sm.customer.taxNumber)," +
            "LOWER(sm.customer.address)," +
            "LOWER(sm.createdBy.userProfile.firstName)," +
            "LOWER(sm.createdBy.userProfile.lastName)," +
            "LOWER(sm.createdBy.email)," +
            "LOWER(sm.updatedBy.userProfile.firstName)," +
            "LOWER(sm.updatedBy.userProfile.lastName)," +
            "LOWER(sm.updatedBy.email)," +
            "LOWER(sm.tax.name)," +
            "LOWER(sm.discount.name)," +
            "LOWER(sm.shippingFee)," +
            "LOWER(sm.total)," +
            "LOWER(sm.subTotal)," +
            "LOWER(sm.amountPaid)," +
            "LOWER(sm.amountDue)," +
            "LOWER(sm.changeAmount)," +
            "LOWER(sm.saleStatus)," +
            "LOWER(sm.paymentStatus)) " +
            "LIKE %:search%")
    Flux<SaleMaster> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT sm FROM SaleMaster sm WHERE sm.tenant.id = :id")
    Flux<SaleMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
