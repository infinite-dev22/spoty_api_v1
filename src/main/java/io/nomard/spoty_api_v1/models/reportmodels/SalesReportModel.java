package io.nomard.spoty_api_v1.models.reportmodels;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;

import java.util.ArrayList;
import java.util.List;

public record SalesReportModel(
// Total number of sale, total revenue from sales, total profit from sales(total cost - total revenue), total cost of products.
        List<DashboardKPIModel> kpis,
        ArrayList<ReportLineChartModel> salesRevenue,  // Graph of sales revenue.
        ArrayList<SaleDetailSummary> productSales,  // Sales per product.
        ArrayList<SaleMasterSummary> customerSales,  // Sales per customer.
        ArrayList<SaleMaster> orders  // Table of sales/orders.
) {
}
