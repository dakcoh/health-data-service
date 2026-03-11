package com.ocare.health.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_sources")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "mode")
    private Integer mode;

    @Column(name = "product_name", length = 100)
    private String productName;

    @Column(name = "product_vender", length = 100)
    private String productVender;

    @Column(name = "source_name", length = 100)
    private String sourceName;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
