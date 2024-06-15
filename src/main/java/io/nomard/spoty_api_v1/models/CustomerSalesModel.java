package io.nomard.spoty_api_v1.models;

import lombok.*;
public interface CustomerSalesModel {
    String getName();
    String getPhone();
    String getEmail();
    Double getTotalSales();
}
