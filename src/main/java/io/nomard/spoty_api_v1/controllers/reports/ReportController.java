package io.nomard.spoty_api_v1.controllers.reports;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.reportmodels.SalesReportModel;
import io.nomard.spoty_api_v1.models.reportmodels.purchases.PurchasesReportModel;
import io.nomard.spoty_api_v1.services.implementations.CurrencyServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ReportServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/reports")
public class ReportController {
    @Autowired
    private CurrencyServiceImpl currencyService;
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

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Currency currency) {
        return currencyService.save(currency);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Currency currency) throws NotFoundException {
        return currencyService.update(currency);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return currencyService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody ArrayList<Long> idList) {
        return currencyService.deleteMultiple(idList);
    }
}