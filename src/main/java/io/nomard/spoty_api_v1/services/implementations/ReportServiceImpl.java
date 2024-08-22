package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.reportmodels.SalesReportModel;
import io.nomard.spoty_api_v1.repositories.adjustments.AdjustmentMasterRepository;
import io.nomard.spoty_api_v1.repositories.purchases.PurchaseMasterRepository;
import io.nomard.spoty_api_v1.repositories.reports.SaleReportRepository;
import io.nomard.spoty_api_v1.repositories.sales.SaleMasterRepository;
import io.nomard.spoty_api_v1.repositories.stock_ins.StockInMasterRepository;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SaleMasterRepository saleMasterRepo;
    @Autowired
    private PurchaseMasterRepository purchaseMasterRepo;
    @Autowired
    private StockInMasterRepository stockInMasterRepo;
    @Autowired
    private AdjustmentMasterRepository adjustmentMasterRepo;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private SaleReportRepository saleReportRepository;

    @Override
    public SalesReportModel getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        try{
            var totalOrders = saleReportRepository.countOrders(authService.authUser().getId(), startDate, endDate);
            var earnings = saleReportRepository.totalEarnings(authService.authUser().getId(), startDate, endDate);
            var costOnProducts = saleReportRepository.totalCostOnProducts(authService.authUser().getId(), startDate, endDate);
            var profit = new DashboardKPIModel("Profit", /*(Double) earnings.getValue() - (Double) costOnProducts.getValue()*/0.00);
            var kpis = List.of(totalOrders, earnings, profit, costOnProducts);

            var salesRevenue = saleReportRepository.getSalesRevenue(authService.authUser().getId(), startDate, endDate);
            var productSales = saleReportRepository.findAllByTenantIdAndCreatedAtBetweenAndGroupByProduct(authService.authUser().getId(), startDate, endDate);
            var customerSales = saleReportRepository.findAllByTenantIdAndCreatedAtBetweenAndGroupByCustomer(authService.authUser().getId(), startDate, endDate);
            var orders = saleReportRepository.findAllByTenantIdAndCreatedAtBetween(authService.authUser().getId(), startDate, endDate);
            return new SalesReportModel(kpis, salesRevenue, productSales, customerSales, orders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<PurchaseMaster> getPurchasesReport(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public Page<StockInMaster> getStockInsReport(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public Page<QuotationMaster> getQuotationsReport(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public Page<AdjustmentMaster> getAdjustmentsReport(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public Page<Account> getAccountsReport(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }
}
