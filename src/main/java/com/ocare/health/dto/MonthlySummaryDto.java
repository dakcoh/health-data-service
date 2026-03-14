package com.ocare.health.dto;

import com.ocare.health.domain.MonthlyHealthSummary;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlySummaryDto {

    private String healthDt;
    private long steps;
    private double calories;
    private double distance;

    public static MonthlySummaryDto from(MonthlyHealthSummary entity) {
        return MonthlySummaryDto.builder()
                .healthDt(entity.getHealthDt())
                .steps(entity.getSteps().longValue())
                .calories(Math.round(entity.getCalories().doubleValue() * 10) / 10.0)
                .distance(Math.round(entity.getDistance().doubleValue() * 100) / 100.0)
                .build();
    }
}
