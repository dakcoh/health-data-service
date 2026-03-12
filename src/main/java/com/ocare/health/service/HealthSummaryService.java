package com.ocare.health.service;

import com.ocare.health.domain.DailyHealthSummary;
import com.ocare.health.domain.HealthEntry;
import com.ocare.health.domain.MonthlyHealthSummary;
import com.ocare.health.repository.DailyHealthSummaryRepository;
import com.ocare.health.repository.MonthlyHealthSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthSummaryService {

    private final DailyHealthSummaryRepository dailyHealthSummaryRepository;
    private final MonthlyHealthSummaryRepository monthlyHealthSummaryRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional
    public void aggregateSummaries(String recordKey, List<HealthEntry> entries) {
        // 날짜별로 데이터 그룹화
        Map<LocalDate, SummaryData> dailyMap = new HashMap<>();
        Map<String, SummaryData> monthlyMap = new HashMap<>();

        for (HealthEntry entry : entries) {
            LocalDate date = entry.getPeriodFrom().toLocalDate();
            String month = date.format(MONTH_FORMATTER);

            // Daily 집계
            dailyMap.computeIfAbsent(date, k -> new SummaryData())
                    .add(entry.getSteps(), entry.getCaloriesValue(), entry.getDistanceValue());

            // Monthly 집계
            monthlyMap.computeIfAbsent(month, k -> new SummaryData())
                    .add(entry.getSteps(), entry.getCaloriesValue(), entry.getDistanceValue());
        }

        // Daily Summary 저장/업데이트
        for (Map.Entry<LocalDate, SummaryData> dailyEntry : dailyMap.entrySet()) {
            DailyHealthSummary summary = dailyHealthSummaryRepository
                    .findByRecordKeyAndHealthDt(recordKey, dailyEntry.getKey())
                    .orElse(DailyHealthSummary.builder()
                            .recordKey(recordKey)
                            .healthDt(dailyEntry.getKey())
                            .steps(BigDecimal.ZERO)
                            .calories(BigDecimal.ZERO)
                            .distance(BigDecimal.ZERO)
                            .build());

            SummaryData data = dailyEntry.getValue();
            summary.addData(data.steps, data.calories, data.distance);
            dailyHealthSummaryRepository.save(summary);
        }

        // Monthly Summary 저장/업데이트
        for (Map.Entry<String, SummaryData> monthlyEntry : monthlyMap.entrySet()) {
            MonthlyHealthSummary summary = monthlyHealthSummaryRepository
                    .findByRecordKeyAndHealthDt(recordKey, monthlyEntry.getKey())
                    .orElse(MonthlyHealthSummary.builder()
                            .recordKey(recordKey)
                            .healthDt(monthlyEntry.getKey())
                            .steps(BigDecimal.ZERO)
                            .calories(BigDecimal.ZERO)
                            .distance(BigDecimal.ZERO)
                            .build());

            SummaryData data = monthlyEntry.getValue();
            summary.addData(data.steps, data.calories, data.distance);
            monthlyHealthSummaryRepository.save(summary);
        }

        log.info("Daily 집계: {}건, Monthly 집계: {}건 업데이트 완료", dailyMap.size(), monthlyMap.size());
    }

    @Transactional(readOnly = true)
    public List<DailyHealthSummary> getDailySummary(String recordKey) {
        return dailyHealthSummaryRepository.findByRecordKeyOrderByHealthDtDesc(recordKey);
    }

    @Transactional(readOnly = true)
    public List<MonthlyHealthSummary> getMonthlySummary(String recordKey) {
        return monthlyHealthSummaryRepository.findByRecordKeyOrderByHealthDtDesc(recordKey);
    }

    // 집계 데이터를 담는 내부 클래스
    private static class SummaryData {
        BigDecimal steps = BigDecimal.ZERO;
        BigDecimal calories = BigDecimal.ZERO;
        BigDecimal distance = BigDecimal.ZERO;

        void add(BigDecimal s, BigDecimal c, BigDecimal d) {
            steps = steps.add(s);
            calories = calories.add(c);
            distance = distance.add(d);
        }
    }
}
