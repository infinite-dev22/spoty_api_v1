package io.nomard.spoty_api_v1.models.reportmodels.purchases;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.reportmodels.ReportLineChartModel;

import java.util.ArrayList;
import java.util.List;

public record PurchasesReportModel(
// Total number of sale, total revenue from sales, total profit from sales(total cost - total revenue), total cost of products.
        List<DashboardKPIModel> kpis,
        ArrayList<ReportLineChartModel> purchasesRevenue,  // Graph of sales revenue.
        ArrayList<PurchaseDetailSummary> productPurchases,  // Purchases per product.
        ArrayList<PurchaseMasterSummary> supplierPurchases,  // Purchases per customer.
        ArrayList<PurchaseMaster> orders  // Table of sales/orders.
) {
}
