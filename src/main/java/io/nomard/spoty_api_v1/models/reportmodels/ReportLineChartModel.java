package io.nomard.spoty_api_v1.models.reportmodels;

import java.time.LocalDateTime;

public record ReportLineChartModel(
        LocalDateTime period,
        Double totalValue
) {
}
