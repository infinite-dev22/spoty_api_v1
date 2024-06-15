package io.nomard.spoty_api_v1.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesModel {
    private String name;
    private Long totalQuantity;
    private Double salePrice;
    private Double costPrice;
}