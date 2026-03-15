package com.ocare.health.service;

import com.ocare.health.domain.DataSource;
import com.ocare.health.domain.HealthEntry;
import com.ocare.health.domain.HealthRecord;
import com.ocare.health.dto.HealthDataRequest;
import com.ocare.health.repository.DataSourceRepository;
import com.ocare.health.repository.HealthEntryRepository;
import com.ocare.health.repository.HealthRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthDataService {

    private final HealthRecordRepository healthRecordRepository;
    private final HealthEntryRepository healthEntryRepository;
    private final DataSourceRepository dataSourceRepository;
    private final HealthSummaryService healthSummaryService;
    private final RedisCacheService redisCacheService;
    private final HealthDataLoader healthDataLoader;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.parse(dateTimeStr, FORMATTER);
        }
    }

    @Transactional
    public void loadJsonData(String fileName, Long userId) throws IOException {
        HealthDataRequest request = healthDataLoader.loadFromFile(fileName);

        HealthRecord healthRecord = saveOrGetHealthRecord(request.getRecordkey(), userId);
        saveOrUpdateDataSource(request.getData().getSource(), healthRecord.getId());
        List<HealthEntry> entries = saveHealthEntries(request.getData().getEntries(), userId, healthRecord.getId());
        
        healthSummaryService.aggregateSummaries(request.getRecordkey(), entries);
        redisCacheService.invalidateUserCache(userId);
        redisCacheService.invalidateSummaryCache(request.getRecordkey());
    }

    private HealthRecord saveOrGetHealthRecord(String recordKey, Long userId) {
        if (healthRecordRepository.existsByRecordKey(recordKey)) {
            HealthRecord record = healthRecordRepository.findByRecordKey(recordKey)
                    .orElseThrow(() -> new IllegalStateException("Record not found"));
            log.info("기존 레코드 사용: {}", recordKey);
            return record;
        }
        
        HealthRecord record = HealthRecord.builder()
                .userId(userId)
                .recordKey(recordKey)
                .build();
        healthRecordRepository.save(record);
        log.info("새 레코드 생성: {}", recordKey);
        return record;
    }

    private void saveOrUpdateDataSource(HealthDataRequest.Source source, Long recordId) {
        if (source == null) {
            return;
        }

        DataSource dataSource = dataSourceRepository.findByRecordId(recordId)
                .orElse(DataSource.builder().recordId(recordId).build());
        
        dataSource.update(
                source.getMode(),
                source.getProduct() != null ? source.getProduct().getName() : null,
                source.getProduct() != null ? source.getProduct().getVender() : null,
                source.getName(),
                source.getType()
        );
        
        dataSourceRepository.save(dataSource);
        log.info("데이터 소스 저장/업데이트: {}", source.getName());
    }

    private List<HealthEntry> saveHealthEntries(List<HealthDataRequest.Entry> entries, Long userId, Long recordId) {
        List<HealthEntry> healthEntries = new ArrayList<>();
        
        for (HealthDataRequest.Entry entry : entries) {
            LocalDateTime periodFrom = parseDateTime(entry.getPeriod().getFrom());
            LocalDateTime periodTo = parseDateTime(entry.getPeriod().getTo());

            HealthEntry healthEntry;
            List<HealthEntry> existing = healthEntryRepository
                    .findByRecordIdAndPeriodFromAndPeriodTo(recordId, periodFrom, periodTo);

            if (!existing.isEmpty()) {
                healthEntry = existing.get(0);
                healthEntry.update(
                        BigDecimal.valueOf(entry.getStepsAsDouble()),
                        BigDecimal.valueOf(entry.getDistance().getValue()),
                        entry.getDistance().getUnit(),
                        BigDecimal.valueOf(entry.getCalories().getValue()),
                        entry.getCalories().getUnit()
                );
            } else {
                healthEntry = HealthEntry.builder()
                        .userId(userId)
                        .recordId(recordId)
                        .periodFrom(periodFrom)
                        .periodTo(periodTo)
                        .steps(BigDecimal.valueOf(entry.getStepsAsDouble()))
                        .distanceValue(BigDecimal.valueOf(entry.getDistance().getValue()))
                        .distanceUnit(entry.getDistance().getUnit())
                        .caloriesValue(BigDecimal.valueOf(entry.getCalories().getValue()))
                        .caloriesUnit(entry.getCalories().getUnit())
                        .build();
            }

            healthEntries.add(healthEntry);
        }

        healthEntryRepository.saveAll(healthEntries);
        log.info("{}개의 엔트리 저장/업데이트 완료", healthEntries.size());
        return healthEntries;
    }

    @Transactional(readOnly = true)
    public List<HealthEntry> getHealthEntriesByUserId(Long userId) {
        List<HealthEntry> cached = redisCacheService.getHealthEntries(userId);
        if (cached != null) {
            return cached;
        }

        List<HealthEntry> entries = healthEntryRepository.findByUserId(userId);
        
        if (!entries.isEmpty()) {
            redisCacheService.cacheHealthEntries(userId, entries);
        }

        return entries;
    }
}
