package io.nomard.spoty_api_v1.repositories.purchases;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.LineChartModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseMasterRepository extends PagingAndSortingRepository<PurchaseMaster, Long>, JpaRepository<PurchaseMaster, Long> {
    @Query("SELECT DATE_FORMAT(e.createdAt, '%Y') AS period, SUM(e.amountPaid) AS totalValue " +
            "FROM PurchaseMaster e " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(e.createdAt, '%Y')")
    List<LineChartModel> yearlyExpenses(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(e.createdAt, '%Y-%m') AS period, SUM(e.amountPaid) AS totalValue " +
            "FROM PurchaseMaster e " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(e.createdAt, '%Y-%m')")
    List<LineChartModel> monthlyExpenses(@Param("id") Long id);

    @Query("SELECT DATE_FORMAT(e.createdAt, '%Y-%m-%u') AS period, SUM(e.amountPaid) AS totalValue " +
            "FROM PurchaseMaster e " +
            "WHERE e.tenant.id = :id " +
            "GROUP BY period " +
            "ORDER BY DATE_FORMAT(e.createdAt, '%Y-%m-%u')")
    List<LineChartModel> weeklyExpenses(@Param("id") Long id);

    List<PurchaseMaster> searchAllByRefContainingIgnoreCaseOrPurchaseStatusContainingIgnoreCaseOrPaymentStatusContainsIgnoreCase(String ref, String purchaseStatus, String paymentStatus);

    @Query("select p from PurchaseMaster p where p.tenant.id = :id")
    Page<PurchaseMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Purchases', SUM(s.amountPaid)) " +
            "FROM PurchaseMaster s " +
            "WHERE s.tenant.id = :id ")
    DashboardKPIModel totalPurchases(@Param("id") Long id);
}
