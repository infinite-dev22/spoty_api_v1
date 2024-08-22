package io.nomard.spoty_api_v1.models.reportmodels;

import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;

public record SaleDetailSummary(
        SaleMaster sale,
        Product product,
        double subTotalPrice,
        Long quantity
) {
}