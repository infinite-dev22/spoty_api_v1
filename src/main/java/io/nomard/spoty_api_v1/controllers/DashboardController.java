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

import java.util.List;

@RestController
@RequestMapping("dashboard")
public class DashboardController {

    @Autowired
    private MainDashboardServiceImpl mainDashboardService;

    @GetMapping("/expense/yearly")
    public List<LineChartModel> expenseYearlySse() {
        return mainDashboardService.getYearlyExpenses();
    }

    @GetMapping("/expense/monthly")
    public List<LineChartModel> expenseMonthlySse() {
        return mainDashboardService.getMonthlyExpenses();
    }

    @GetMapping("/expense/weekly")
    public List<LineChartModel> expenseWeeklySse() {
        return mainDashboardService.getWeeklyExpenses();
    }

    @GetMapping("/income/yearly")
    public List<LineChartModel> incomeYearlySse() {
        return mainDashboardService.getYearlyIncomes();
    }

    @GetMapping("/income/monthly")
    public List<LineChartModel> incomeMonthlySse() {
        return mainDashboardService.getMonthlyIncomes();
    }

    @GetMapping("/income/weekly")
    public List<LineChartModel> incomeWeeklySse() {
        return mainDashboardService.getWeeklyIncomes();
    }

    @GetMapping("/top/products")
    public List<ProductSalesModel> topProductsSold(@RequestParam(defaultValue = "10") Integer limit) {
        return mainDashboardService.getTopProductsSold(limit);
    }

    @GetMapping("/recent/orders")
    public List<SaleMaster> recentOrders(@RequestParam(defaultValue = "10") Integer limit) {
        return mainDashboardService.getRecentOrders(limit);
    }

    @GetMapping("/stock/alerts")
    public List<StockAlertModel> stockAlerts() {
        return mainDashboardService.getProductsStockAlert();
    }

    @GetMapping("/kpis")
    public List<DashboardKPIModel> dashboardKPIs() {
        return mainDashboardService.getDashboardKPI();
    }
}