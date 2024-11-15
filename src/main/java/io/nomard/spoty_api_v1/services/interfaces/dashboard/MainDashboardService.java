package io.nomard.spoty_api_v1.services.interfaces.dashboard;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.LineChartModel;
import io.nomard.spoty_api_v1.models.ProductSalesModel;
import io.nomard.spoty_api_v1.models.StockAlertModel;

import java.util.List;

public interface MainDashboardService {
    // Expenses.
    List<LineChartModel> getYearlyExpenses();

    List<LineChartModel> getMonthlyExpenses();

    List<LineChartModel> getWeeklyExpenses();

    // Incomes.
    List<LineChartModel> getYearlyIncomes();

    List<LineChartModel> getMonthlyIncomes();

    List<LineChartModel> getWeeklyIncomes();

    List<LineChartModel> getYearlyRevenue();

    List<LineChartModel> getMonthlyRevenue();

    List<LineChartModel> getWeeklyRevenue();

    List<ProductSalesModel> getTopProductsSold(Integer limit);

    List<SaleMaster> getRecentOrders(Integer limit);

    List<StockAlertModel> getProductsStockAlert();

    Number getTotalSalesKPI();

    Number getRevenueKPI();

    Number getSaleCostKPI();

    Number getProfitKPI();

    Number getPurchasesKPI();

    Number getPurchaseCostKPI();

    Number getCancelledOrdersKPI();

    Number getPurchaseReturnsKPI();

    Number getStockQuantityAtHandKPI();

    Number getStockQuantityValueKPI();

    Number getLowStockItemsKPI();

    Number getTotalProductCategoriesKPI();

    Number getProductsKPI();

    Number getCustomersKPI();

    Number getSuppliersKPI();
}
