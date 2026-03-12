package com.ocare.health.repository;

import com.ocare.health.domain.DailyHealthSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyHealthSummaryRepository extends JpaRepository<DailyHealthSummary, Long> {
    Optional<DailyHealthSummary> findByRecordKeyAndHealthDt(String recordKey, LocalDate healthDt);
    List<DailyHealthSummary> findByRecordKeyOrderByHealthDtDesc(String recordKey);
}
