package com.ocare.health.repository;

import com.ocare.health.domain.MonthlyHealthSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyHealthSummaryRepository extends JpaRepository<MonthlyHealthSummary, Long> {
    Optional<MonthlyHealthSummary> findByRecordKeyAndHealthDt(String recordKey, String healthDt);
    List<MonthlyHealthSummary> findByRecordKeyOrderByHealthDtDesc(String recordKey);
}
