package io.nomard.spoty_api_v1.controllers.client.api.reports;

import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.models.reportmodels.SalesReportModel;
import io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchasesReportModel;
import io.nomard.spoty_api_v1.services.implementations.ReportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/reports")
public class ReportController {
    @Autowired
    private ReportServiceImpl reportService;

    @GetMapping("/sales")
    public SalesReportModel salesReport(@RequestParam LocalDateTime startDate,
                                        @RequestParam LocalDateTime endDate) {
        return reportService.getSalesReport(startDate, endDate);
    }

    @GetMapping("/purchases")
    public PurchasesReportModel purchasesReport(@RequestParam LocalDateTime startDate,
                                                @RequestParam LocalDateTime endDate) {
        return reportService.getPurchasesReport(startDate, endDate);
    }

    @GetMapping("/accounts/receivable")
    public ArrayList<AccountTransaction> getByContains(@RequestParam LocalDateTime startDate,
                                                       @RequestParam LocalDateTime endDate) {
        return reportService.getAccountsReport(startDate, endDate);
    }
}
