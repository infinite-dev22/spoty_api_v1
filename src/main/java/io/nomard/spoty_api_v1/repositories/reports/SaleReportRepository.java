package io.nomard.spoty_api_v1.repositories.reports;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.reportmodels.ReportLineChartModel;
import io.nomard.spoty_api_v1.models.reportmodels.SaleDetailSummary;
import io.nomard.spoty_api_v1.models.reportmodels.SaleMasterSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public interface SaleReportRepository extends JpaRepository<SaleMaster, Long> {
    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Orders', COUNT(s)) " +
            "FROM SaleMaster s " +
            "WHERE s.tenant.id = :id AND s.createdAt BETWEEN :startDate AND :endDate")
    DashboardKPIModel countOrders(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Earnings', SUM(s.amountPaid)) " +
            "FROM SaleMaster s " +
            "WHERE s.tenant.id = :id AND s.createdAt BETWEEN :startDate AND :endDate")
    DashboardKPIModel totalEarnings(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Product Costs', SUM(p.cost)) " +
            "FROM PurchaseDetail p " +
            "WHERE p.purchase.tenant.id = :id AND p.createdAt BETWEEN :startDate AND :endDate")
    DashboardKPIModel totalCostOnProducts(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.ReportLineChartModel(sm.createdAt, SUM(sm.amountPaid)) " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND sm.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY sm.createdAt " +
            "ORDER BY sm.createdAt")
    ArrayList<ReportLineChartModel> getSalesRevenue(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.SaleDetailSummary(p.sale, p.product, SUM(p.subTotalPrice), SUM(p.quantity)) " +
            "FROM SaleDetail p " +
            "WHERE p.sale.tenant.id = :id AND p.sale.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.product, p.sale")
    ArrayList<SaleDetailSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByProduct(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /*@Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.UserSale(p.sale, p.sale.customer, SUM(p.subTotalPrice), SUM(p.quantity)) " +
            "FROM SaleDetail p " +
            "WHERE p.sale.tenant.id = :id AND p.sale.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.sale.customer, p.sale")
    ArrayList<UserSale> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );*/

    // Can work better for Top Customers
    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.SaleMasterSummary(p, p.customer, COUNT(p)) " +
            "FROM SaleMaster p " +
            "WHERE p.tenant.id = :id AND p.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.customer, p")
    ArrayList<SaleMasterSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("select p from SaleMaster p where p.tenant.id = :id AND p.createdAt BETWEEN :startDate AND :endDate")
    ArrayList<SaleMaster> findAllByTenantIdAndCreatedAtBetween(@Param("id") Long id, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
