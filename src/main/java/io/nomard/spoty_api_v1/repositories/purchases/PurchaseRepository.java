package io.nomard.spoty_api_v1.repositories.purchases;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.LineChartModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseRepository extends ReactiveSortingRepository<PurchaseMaster, Long>, ReactiveCrudRepository<PurchaseMaster, Long> {
    @Query("SELECT DATE_FORMAT(pm.createdAt, '%Y') AS period, SUM(pm.amountPaid) AS totalValue " +
            "FROM PurchaseMaster pm " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(pm.createdAt, '%Y')")
    Flux<LineChartModel> yearlyExpenses(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(CONCAT(YEAR(CURDATE()), '-', months.month, '-01'), '%Y %M') AS period, " +
            "COALESCE(SUM(pm.amountPaid), 0) AS totalValue " +
            "FROM ( " +
            "    SELECT '01' AS month UNION ALL SELECT '02' UNION ALL SELECT '03' UNION ALL SELECT '04' UNION ALL " +
            "    SELECT '05' UNION ALL SELECT '06' UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL " +
            "    SELECT '09' UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL SELECT '12' " +
            ") AS months " +
            "LEFT JOIN PurchaseMaster pm ON DATE_FORMAT(pm.createdAt, '%Y %M') = DATE_FORMAT(CONCAT(YEAR(CURDATE()), '-', months.month, '-01'), '%Y %M') " +
            "AND pm.tenant.id = :id " +
            "GROUP BY months.month " +
            "ORDER BY FIELD(DATE_FORMAT(CONCAT(YEAR(CURDATE()), '-', months.month, '-01'), '%M'), " +
            "'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December') " +
            "LIMIT 12")
    Flux<LineChartModel> monthlyExpenses(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(pm.createdAt, '%Y-%m-%u') AS period, SUM(pm.amountPaid) AS totalValue " +
            "FROM PurchaseMaster pm " +
            "WHERE pm.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(pm.createdAt, '%Y-%m-%u')")
    Flux<LineChartModel> weeklyExpenses(@Param("id") Long id);

    @Query("SELECT pm " +
            "FROM PurchaseMaster pm " +
            "WHERE pm.tenant.id = :id " +
            "AND CONCAT(LOWER(pm.ref)," +
            "LOWER(pm.supplier.name)," +
            "LOWER(pm.supplier.email)," +
            "LOWER(pm.supplier.phone)," +
            "LOWER(pm.supplier.taxNumber)," +
            "LOWER(pm.supplier.address)," +
            "LOWER(pm.createdBy.userProfile.firstName)," +
            "LOWER(pm.createdBy.userProfile.lastName)," +
            "LOWER(pm.createdBy.email)," +
            "LOWER(pm.updatedBy.userProfile.firstName)," +
            "LOWER(pm.updatedBy.userProfile.lastName)," +
            "LOWER(pm.updatedBy.email)," +
            "LOWER(pm.tax.name)," +
            "LOWER(pm.discount.name)," +
            "LOWER(pm.shippingFee)," +
            "LOWER(pm.total)," +
            "LOWER(pm.subTotal)," +
            "LOWER(pm.amountPaid)," +
            "LOWER(pm.amountDue)," +
            "LOWER(pm.purchaseStatus)," +
            "LOWER(pm.paymentStatus)) " +
            "LIKE %:search%")
    Flux<PurchaseMaster> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT pm FROM PurchaseMaster pm WHERE pm.tenant.id = :id")
    Flux<PurchaseMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Purchases', SUM(pm.amountPaid)) " +
            "FROM PurchaseMaster pm " +
            "WHERE pm.tenant.id = :id ")
    Mono<DashboardKPIModel> totalPurchases(@Param("id") Long id);
}
