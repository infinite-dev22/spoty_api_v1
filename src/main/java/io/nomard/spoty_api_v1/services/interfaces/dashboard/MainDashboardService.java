package io.nomard.spoty_api_v1.services.interfaces.dashboard;

import io.nomard.spoty_api_v1.entities.sales.*;
import io.nomard.spoty_api_v1.models.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MainDashboardService {
    // Expenses.
    Flux<LineChartModel> getYearlyExpenses();

    Flux<LineChartModel> getMonthlyExpenses();

    Flux<LineChartModel> getWeeklyExpenses();
    // Incomes.
    Flux<LineChartModel> getYearlyIncomes();

    Flux<LineChartModel> getMonthlyIncomes();

    Flux<LineChartModel> getMonthlyRevenue();

    Flux<LineChartModel> getWeeklyRevenue();

    Flux<ProductSalesModel> getTopProductsSold(Integer limit);

    Flux<SaleMaster> getRecentOrders(Integer limit);

    Flux<StockAlertModel> getProductsStockAlert();

    Mono<DashboardKPIModel> getTotalEarningsKPI();
    Mono<DashboardKPIModel> getTotalPurchasesKPI();
    Mono<DashboardKPIModel> getCountProductsKPI();
    Mono<DashboardKPIModel> getCountCustomersKPI();
    Mono<DashboardKPIModel> getCountSuppliersKPI();
}
