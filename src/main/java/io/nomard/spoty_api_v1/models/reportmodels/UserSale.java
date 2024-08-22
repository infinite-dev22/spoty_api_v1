package io.nomard.spoty_api_v1.models.reportmodels;

import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;

public record UserSale(
        SaleMaster sale,
        Customer customer,
        double subTotalPrice,
        Long quantity
) {
}