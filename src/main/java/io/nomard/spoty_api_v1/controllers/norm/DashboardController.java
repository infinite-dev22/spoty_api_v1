package io.nomard.spoty_api_v1.controllers.norm;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
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

    @GetMapping("/revenue/yearly")
    public List<LineChartModel> revenueYearlySse() {
        return mainDashboardService.getYearlyRevenue();
    }

    @GetMapping("/revenue/monthly")
    public List<LineChartModel> revenueMonthlySse() {
        return mainDashboardService.getMonthlyRevenue();
    }

    @GetMapping("/revenue/weekly")
    public List<LineChartModel> revenueWeeklySse() {
        return mainDashboardService.getWeeklyRevenue();
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

    @GetMapping("kpi/total/sales")
    public Number getTotalSalesKPI() {
        return mainDashboardService.getTotalSalesKPI();
    }

    @GetMapping("kpi/revenue")
    public Number getRevenueKPI() {
        return mainDashboardService.getRevenueKPI();
    }

    @GetMapping("kpi/sale/cost")
    public Number getSaleCostKPI() {
        return mainDashboardService.getSaleCostKPI();
    }

    @GetMapping("kpi/profit")
    public Number getProfitKPI() {
        return mainDashboardService.getProfitKPI();
    }

    @GetMapping("kpi/purchases")
    public Number getPurchasesKPI() {
        return mainDashboardService.getPurchasesKPI();
    }

    @GetMapping("kpi/purchase/cost")
    public Number getPurchaseCostKPI() {
        return mainDashboardService.getPurchaseCostKPI();
    }

    @GetMapping("kpi/cancelled/orders")
    public Number getCancelledOrdersKPI() {
        return mainDashboardService.getCancelledOrdersKPI();
    }

    @GetMapping("kpi/purchase/returns")
    public Number getPurchaseReturnsKPI() {
        return mainDashboardService.getPurchaseReturnsKPI();
    }

    @GetMapping("kpi/product/quantity/at_hand")
    public Number getStockQuantityAtHandKPI() {
        return mainDashboardService.getStockQuantityAtHandKPI();
    }

    @GetMapping("kpi/product/quantity/value")
    public Number getStockQuantityValueKPI() {
        return mainDashboardService.getStockQuantityValueKPI();
    }

    @GetMapping("kpi/product/low/stock")
    public Number getLowStockItemsKPI() {
        return mainDashboardService.getLowStockItemsKPI();
    }

    @GetMapping("kpi/products/categories")
    public Number getTotalProductCategoriesKPI() {
        return mainDashboardService.getTotalProductCategoriesKPI();
    }

    @GetMapping("kpi/products")
    public Number getProductsKPI() {
        return mainDashboardService.getProductsKPI();
    }

    @GetMapping("kpi/customers")
    public Number getCustomersKPI() {
        return mainDashboardService.getCustomersKPI();
    }

    @GetMapping("kpi/suppliers")
    public Number getSuppliersKPI() {
        return mainDashboardService.getSuppliersKPI();
    }
}
