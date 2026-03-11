package com.ocare.health.repository;

import com.ocare.health.domain.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    Optional<HealthRecord> findByRecordKey(String recordKey);
    boolean existsByRecordKey(String recordKey);
}
