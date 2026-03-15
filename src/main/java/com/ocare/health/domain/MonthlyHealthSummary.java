package com.ocare.health.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_health_summary")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyHealthSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_key", nullable = false, length = 36)
    private String recordKey;

    @Column(name = "health_dt", nullable = false, length = 7)
    private String healthDt; // YYYY-MM 형식

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
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
    }
}
