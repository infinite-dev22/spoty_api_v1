package io.nomard.spoty_api_v1.models.reportmodels.purchases;

import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;

public record PurchaseDetailSummary(
        PurchaseMaster purchase,
        Product product,
        double subTotalPrice,
        Long quantity
) {
}