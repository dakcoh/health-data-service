package com.ocare.health.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_health_summary")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyHealthSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_key", nullable = false, length = 36)
    private String recordKey;

    @Column(name = "health_dt", nullable = false)
    private LocalDate healthDt;

    @Column(name = "steps", nullable = false, precision = 12, scale = 2)
    private BigDecimal steps;

    @Column(name = "calories", nullable = false, precision = 12, scale = 2)
    private BigDecimal calories;

    @Column(name = "distance", nullable = false, precision = 12, scale = 5)
    private BigDecimal distance;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addData(BigDecimal steps, BigDecimal calories, BigDecimal distance) {
        this.steps = this.steps.add(steps);
        this.calories = this.calories.add(calories);
        this.distance = this.distance.add(distance);
    }
}
