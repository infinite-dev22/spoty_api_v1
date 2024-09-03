package io.nomard.spoty_api_v1.services.interfaces;

import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.models.reportmodels.SalesReportModel;
import io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchasesReportModel;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface ReportService {
    SalesReportModel getSalesReport(LocalDateTime startDate, LocalDateTime endDate);
    PurchasesReportModel getPurchasesReport(LocalDateTime startDate, LocalDateTime endDate);
    Page<StockInMaster> getStockInsReport(LocalDateTime startDate, LocalDateTime endDate);
    Page<QuotationMaster> getQuotationsReport(LocalDateTime startDate, LocalDateTime endDate);
    Page<AdjustmentMaster> getAdjustmentsReport(LocalDateTime startDate, LocalDateTime endDate);
    ArrayList<AccountTransaction> getAccountsReport(LocalDateTime startDate, LocalDateTime endDate);
}
