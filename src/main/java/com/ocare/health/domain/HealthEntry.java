package com.ocare.health.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_entries")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "period_from", nullable = false)
    private LocalDateTime periodFrom;

    @Column(name = "period_to", nullable = false)
    private LocalDateTime periodTo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal steps;

    @Column(name = "distance_value", nullable = false, precision = 10, scale = 5)
    private BigDecimal distanceValue;

    @Column(name = "distance_unit", nullable = false, length = 10)
    private String distanceUnit;

    @Column(name = "calories_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal caloriesValue;

    @Column(name = "calories_unit", nullable = false, length = 10)
    private String caloriesUnit;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void update(BigDecimal steps, BigDecimal distanceValue, String distanceUnit,
                       BigDecimal caloriesValue, String caloriesUnit) {
        this.steps = steps;
        this.distanceValue = distanceValue;
        this.distanceUnit = distanceUnit;
        this.caloriesValue = caloriesValue;
        this.caloriesUnit = caloriesUnit;
    }
}
