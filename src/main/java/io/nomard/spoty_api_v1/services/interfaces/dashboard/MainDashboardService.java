package io.nomard.spoty_api_v1.services.interfaces.dashboard;

import io.nomard.spoty_api_v1.entities.sales.*;
import io.nomard.spoty_api_v1.models.*;

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

    List<ProductSalesModel> getTopProductsSold(Integer limit);

    List<SaleMaster> getRecentOrders(Integer limit);

    List<StockAlertModel> getProductsStockAlert();

    List<DashboardKPIModel> getDashboardKPI();
}
