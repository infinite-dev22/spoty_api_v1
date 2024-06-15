package io.nomard.spoty_api_v1.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertModel {
    private String name;
    private Long totalQuantity;
    private Double costPrice;
}