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

import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public interface PurchaseReportRepository extends JpaRepository<PurchaseMaster, Long> {
    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Purchases', COUNT(s)) " +
            "FROM PurchaseMaster s " +
            "WHERE s.tenant.id = :id AND DATE_FORMAT(s.createdAt, 'YYYY-MM-dd') BETWEEN DATE_FORMAT(:startDate, 'YYYY-MM-dd') AND DATE_FORMAT(:endDate, 'YYYY-MM-dd')")
    DashboardKPIModel countPurchases(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Expenditures', SUM(s.amountPaid)) " +
            "FROM PurchaseMaster s " +
            "WHERE s.tenant.id = :id AND DATE_FORMAT(s.createdAt, 'YYYY-MM-dd') BETWEEN DATE_FORMAT(:startDate, 'YYYY-MM-dd') AND DATE_FORMAT(:endDate, 'YYYY-MM-dd')")
    DashboardKPIModel totalExpenditures(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Product Costs', SUM(p.cost)) " +
            "FROM PurchaseDetail p " +
            "WHERE p.purchase.tenant.id = :id AND DATE_FORMAT(p.createdAt, 'YYYY-MM-dd') BETWEEN DATE_FORMAT(:startDate, 'YYYY-MM-dd') AND DATE_FORMAT(:endDate, 'YYYY-MM-dd')")
    DashboardKPIModel totalCostOnProducts(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.ReportLineChartModel(sm.createdAt, SUM(sm.amountPaid)) " +
            "FROM PurchaseMaster sm " +
            "WHERE sm.tenant.id = :id AND DATE_FORMAT(sm.createdAt, 'YYYY-MM-dd') BETWEEN DATE_FORMAT(:startDate, 'YYYY-MM-dd') AND DATE_FORMAT(:endDate, 'YYYY-MM-dd') " +
            "GROUP BY DATE_FORMAT(sm.createdAt, 'YYYY-MM-dd') " +
            "ORDER BY DATE_FORMAT(sm.createdAt, 'YYYY-MM-dd')")
    ArrayList<ReportLineChartModel> getPurchasesRevenue(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchaseDetailSummary(p.purchase, p.product, SUM(p.subTotalCost), SUM(p.quantity)) " +
            "FROM PurchaseDetail p " +
            "WHERE p.purchase.tenant.id = :id AND DATE_FORMAT(p.purchase.createdAt, 'YYYY-MM-dd') BETWEEN DATE_FORMAT(:startDate, 'YYYY-MM-dd') AND DATE_FORMAT(:endDate, 'YYYY-MM-dd') " +
            "GROUP BY p.product, p.purchase")
    ArrayList<PurchaseDetailSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByProduct(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /*@Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.UserPurchase(p.purchase, p.purchase.supplier, SUM(p.subTotalPrice), SUM(p.quantity)) " +
            "FROM PurchaseDetail p " +
            "WHERE p.purchase.tenant.id = :id AND p.purchase.createdAt BETWEEN DATE_FORMAT(:startDate, 'YYYY-MM-dd') AND DATE_FORMAT(:endDate, 'YYYY-MM-dd') " +
            "GROUP BY p.purchase.supplier, p.purchase")
    ArrayList<UserPurchase> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );*/

    // Can work better for Top Customers
    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchaseMasterSummary(p, p.supplier, COUNT(p)) " +
            "FROM PurchaseMaster p " +
            "WHERE p.tenant.id = :id AND DATE_FORMAT(p.createdAt, 'YYYY-MM-dd') BETWEEN DATE_FORMAT(:startDate, 'YYYY-MM-dd') AND DATE_FORMAT(:endDate, 'YYYY-MM-dd') " +
            "GROUP BY p.supplier, p")
    ArrayList<PurchaseMasterSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("select p from PurchaseMaster p where p.tenant.id = :id AND DATE_FORMAT(p.createdAt, 'YYYY-MM-dd') BETWEEN DATE_FORMAT(:startDate, 'YYYY-MM-dd') AND DATE_FORMAT(:endDate, 'YYYY-MM-dd')")
    ArrayList<PurchaseMaster> findAllByTenantIdAndCreatedAtBetween(@Param("id") Long id, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
