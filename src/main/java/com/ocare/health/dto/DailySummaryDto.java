package com.ocare.health.dto;

import com.ocare.health.domain.DailyHealthSummary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailySummaryDto {

    private LocalDate healthDt;
    private long steps;
    private double calories;
    private double distance;

    public static DailySummaryDto from(DailyHealthSummary entity) {
        return DailySummaryDto.builder()
                .healthDt(entity.getHealthDt())
                .steps(entity.getSteps().longValue())
                .calories(Math.round(entity.getCalories().doubleValue() * 10) / 10.0)
                .distance(Math.round(entity.getDistance().doubleValue() * 100) / 100.0)
                .build();
    }
}
