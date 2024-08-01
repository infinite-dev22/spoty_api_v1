package io.nomard.spoty_api_v1.models;

import java.math.BigDecimal;

public interface LineChartModel {
    String getPeriod();

    BigDecimal getTotalValue();
}
