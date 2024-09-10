package io.nomard.spoty_api_v1.repositories.reports;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.reportmodels.ReportLineChartModel;
import io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchaseDetailSummary;
import io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchaseMasterSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public interface PurchaseReportRepository extends JpaRepository<PurchaseMaster, Long> {
    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Purchases', COUNT(s)) " +
            "FROM PurchaseMaster s " +
            "WHERE s.tenant.id = :id AND CAST(s.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel countPurchases(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Expenditures', SUM(s.amountPaid)) " +
            "FROM PurchaseMaster s " +
            "WHERE s.tenant.id = :id AND CAST(s.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel totalExpenditures(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Product Costs', SUM(pm.unitCost)) " +
            "FROM PurchaseDetail pm " +
            "WHERE pm.purchase.tenant.id = :id AND CAST(pm.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel totalCostOnProducts(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.ReportLineChartModel(CAST(pm.createdAt AS DATE), SUM(pm.amountPaid)) " +
            "FROM PurchaseMaster pm " +
            "WHERE pm.tenant.id = :id AND CAST(pm.createdAt AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(pm.createdAt AS DATE) " +
            "ORDER BY CAST(pm.createdAt AS DATE)")
    ArrayList<ReportLineChartModel> getPurchasesRevenue(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchaseDetailSummary(pm.purchase, pm.product, SUM(pm.unitCost), SUM(pm.quantity)) " +
            "FROM PurchaseDetail pm " +
            "WHERE pm.purchase.tenant.id = :id AND CAST(pm.purchase.createdAt AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY pm.product, pm.purchase")
    ArrayList<PurchaseDetailSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByProduct(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /*@Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.UserPurchase(pm.purchase, pm.purchase.supplier, SUM(pm.subTotalPrice), SUM(pm.quantity)) " +
            "FROM PurchaseDetail pm " +
            "WHERE pm.purchase.tenant.id = :id AND pm.purchase.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY pm.purchase.supplier, pm.purchase")
    ArrayList<UserPurchase> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );*/

    // Can work better for Top Customers
    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchaseMasterSummary(pm, pm.supplier, COUNT(pm)) " +
            "FROM PurchaseMaster pm " +
            "WHERE pm.tenant.id = :id AND CAST(pm.createdAt AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY pm.supplier, pm")
    ArrayList<PurchaseMasterSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("select pm from PurchaseMaster pm where pm.tenant.id = :id AND CAST(pm.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    ArrayList<PurchaseMaster> findAllByTenantIdAndCreatedAtBetween(@Param("id") Long id, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
