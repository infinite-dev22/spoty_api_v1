package io.nomard.spoty_api_v1.repositories.reports;

import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public interface AccountsReportRepository extends JpaRepository<Account, Long> {
    @Query("SELECT at " +
            "FROM AccountTransaction at " +
            "WHERE at.tenant.id = :id " +
            "AND at.transactionType LIKE '%sale%' " +
            "AND CAST(at.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    ArrayList<AccountTransaction> accountsReceivable(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /*@Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Purchases', COUNT(s)) " +
            "FROM Account s " +
            "WHERE s.tenant.id = :id AND CAST(s.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel countPurchases(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Expenditures', SUM(s.amountPaid)) " +
            "FROM Account s " +
            "WHERE s.tenant.id = :id AND CAST(s.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel totalExpenditures(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Product Costs', SUM(pm.cost)) " +
            "FROM PurchaseDetail pm " +
            "WHERE pm.purchase.tenant.id = :id AND CAST(pm.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    DashboardKPIModel totalCostOnProducts(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.ReportLineChartModel(CAST(pm.createdAt AS DATE), SUM(pm.amountPaid)) " +
            "FROM Account pm " +
            "WHERE pm.tenant.id = :id AND CAST(pm.createdAt AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(pm.createdAt AS DATE) " +
            "ORDER BY CAST(pm.createdAt AS DATE)")
    ArrayList<ReportLineChartModel> getPurchasesRevenue(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchaseDetailSummary(pm.purchase, pm.product, SUM(pm.subTotalCost), SUM(pm.quantity)) " +
            "FROM PurchaseDetail pm " +
            "WHERE pm.purchase.tenant.id = :id AND CAST(pm.purchase.createdAt AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY pm.product, pm.purchase")
    ArrayList<PurchaseDetailSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByProduct(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    *//*@Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.UserPurchase(pm.purchase, pm.purchase.supplier, SUM(pm.subTotalPrice), SUM(pm.quantity)) " +
            "FROM PurchaseDetail pm " +
            "WHERE pm.purchase.tenant.id = :id AND pm.purchase.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY pm.purchase.supplier, pm.purchase")
    ArrayList<UserPurchase> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );*//*

    // Can work better for Top Customers
    @Query("SELECT new io.nomard.spoty_api_v1.models.reportmodels.purchases.AccountSummary(pm, pm.supplier, COUNT(pm)) " +
            "FROM Account pm " +
            "WHERE pm.tenant.id = :id AND CAST(pm.createdAt AS DATE) BETWEEN :startDate AND :endDate " +
            "GROUP BY pm.supplier, pm")
    ArrayList<AccountSummary> findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(
            @Param("id") Long id,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("select pm from Account pm where pm.tenant.id = :id AND CAST(pm.createdAt AS DATE) BETWEEN :startDate AND :endDate")
    ArrayList<Account> findAllByTenantIdAndCreatedAtBetween(@Param("id") Long id, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);*/
}
