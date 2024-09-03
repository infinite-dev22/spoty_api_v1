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

import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public interface SaleReportRepository extends JpaRepository<SaleMaster, Long> {
    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Orders', COUNT(sm)) " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND CAST(sm.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel countOrders(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Earnings', SUM(sm.amountPaid)) " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND CAST(sm.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel totalEarnings(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Product Costs', SUM(pd.cost)) " +
            "FROM PurchaseDetail pd " +
            "WHERE pd.purchase.tenant.id = :id AND CAST(pd.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel totalCostOnProducts(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(sm.amountPaid), 0.0) " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND CAST(sm.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    Double getSales(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) " +
            "FROM Expense e " +
            "WHERE e.tenant.id = :id AND CAST(e.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    Double getExpenses(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.ReportLineChartModel(CAST(sm.createdAt AS date), SUM(sm.amountPaid)) " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND CAST(sm.createdAt AS date) BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(sm.createdAt AS date) " +
            "ORDER BY CAST(sm.createdAt AS date)")
    ArrayList<ReportLineChartModel> getSalesRevenue(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate, // Note the change to LocalDate
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.SaleDetailSummary(pd.sale, pd.product, SUM(pd.subTotalPrice), SUM(pd.quantity)) " +
            "FROM SaleDetail pd " +
            "WHERE pd.sale.tenant.id = :id AND CAST(pd.sale.createdAt AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY pd.product, pd.sale")
    ArrayList<SaleDetailSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByProduct(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /*@Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.UserSale(p.sale, p.sale.customer, SUM(p.subTotalPrice), SUM(p.quantity)) " +
            "FROM SaleDetail p " +
            "WHERE p.sale.tenant.id = :id AND CAST(p.sale AS DATE.createdAt) BETWEEN :startDate AND :endDate " +
            "GROUP BY p.sale.customer, p.sale")
    ArrayList<UserSale> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );*/

    // Can work better for Top Customers
    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.SaleMasterSummary(sm, sm.customer, COUNT(sm)) " +
            "FROM SaleMaster sm " +
            "WHERE sm.tenant.id = :id AND CAST(sm.createdAt AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY sm.customer, sm")
    ArrayList<SaleMasterSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("select sm from SaleMaster sm where sm.tenant.id = :id AND CAST(sm.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    ArrayList<SaleMaster> findAllByTenantIdAndCreatedAtBetween(@Param("id") Long id, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
