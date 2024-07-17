package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.LineChartModel;
import io.nomard.spoty_api_v1.models.ProductSalesModel;
import io.nomard.spoty_api_v1.models.StockAlertModel;
import io.nomard.spoty_api_v1.services.implementations.dashboard.MainDashboardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("dashboard")
public class DashboardController {

    @Autowired
    private MainDashboardServiceImpl mainDashboardService;

    @GetMapping("/expense/yearly")
    public Flux<LineChartModel> expenseYearlySse() {
        return mainDashboardService.getYearlyExpenses();
    }

    @GetMapping("/expense/monthly")
    public Flux<LineChartModel> expenseMonthlySse() {
        return mainDashboardService.getMonthlyExpenses();
    }

    @GetMapping("/expense/weekly")
    public Flux<LineChartModel> expenseWeeklySse() {
        return mainDashboardService.getWeeklyExpenses();
    }

    @GetMapping("/income/yearly")
    public Flux<LineChartModel> incomeYearlySse() {
        return mainDashboardService.getYearlyIncomes();
    }

    @GetMapping("/income/monthly")
    public Flux<LineChartModel> incomeMonthlySse() {
        return mainDashboardService.getMonthlyIncomes();
    }

    @GetMapping("/revenue/monthly")
    public Flux<LineChartModel> revenueMonthlySse() {
        return mainDashboardService.getMonthlyRevenue();
    }

    @GetMapping("/revenue/weekly")
    public Flux<LineChartModel> revenueWeeklySse() {
        return mainDashboardService.getWeeklyRevenue();
    }

    @GetMapping("/top/products")
    public Flux<ProductSalesModel> topProductsSold(@RequestParam(defaultValue = "10") Integer limit) {
        return mainDashboardService.getTopProductsSold(limit);
    }

    @GetMapping("/recent/orders")
    public Flux<SaleMaster> recentOrders(@RequestParam(defaultValue = "10") Integer limit) {
        return mainDashboardService.getRecentOrders(limit);
    }

    @GetMapping("/stock/alerts")
    public Flux<StockAlertModel> stockAlerts() {
        return mainDashboardService.getProductsStockAlert();
    }

    @GetMapping("/kpi/earnings")
    public Mono<DashboardKPIModel> totalEarningsKPI() {
        return mainDashboardService.getTotalEarningsKPI();
    }

    @GetMapping("/kpi/purchases")
    public Mono<DashboardKPIModel> totalPurchasesKPI() {
        return mainDashboardService.getTotalPurchasesKPI();
    }

    @GetMapping("/kpi/products")
    public Mono<DashboardKPIModel> countProductsKPI() {
        return mainDashboardService.getCountProductsKPI();
    }

    @GetMapping("/kpi/customers")
    public Mono<DashboardKPIModel> countCustomersKPI() {
        return mainDashboardService.getCountCustomersKPI();
    }

    @GetMapping("/kpi/suppliers")
    public Mono<DashboardKPIModel> countSuppliersKPI() {
        return mainDashboardService.getCountSuppliersKPI();
    }
}
