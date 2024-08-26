package io.nomard.spoty_api_v1.models.reportmodels.purchases;

import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;

public record PurchaseMasterSummary(
        PurchaseMaster purchase,
        Supplier supplier,
        Long total
) {}