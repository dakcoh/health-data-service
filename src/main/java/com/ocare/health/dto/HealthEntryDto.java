package com.ocare.health.dto;

import com.ocare.health.domain.HealthEntry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HealthEntryDto {

    private LocalDateTime periodFrom;
    private LocalDateTime periodTo;
    private long steps;
    private double distance;
    private String distanceUnit;
    private double calories;
    private String caloriesUnit;

    public static HealthEntryDto from(HealthEntry entity) {
        return HealthEntryDto.builder()
                .periodFrom(entity.getPeriodFrom())
                .periodTo(entity.getPeriodTo())
                .steps(entity.getSteps().longValue())
                .distance(Math.round(entity.getDistanceValue().doubleValue() * 100) / 100.0)
                .distanceUnit(entity.getDistanceUnit())
                .calories(Math.round(entity.getCaloriesValue().doubleValue() * 10) / 10.0)
                .caloriesUnit(entity.getCaloriesUnit())
                .build();
    }
}
