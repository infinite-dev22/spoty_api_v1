package io.nomard.spoty_api_v1.models.reportmodels;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public record ReportLineChartModel(
        Date period,
        Double totalValue
) {
}
