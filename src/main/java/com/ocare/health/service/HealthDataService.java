package com.ocare.health.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocare.health.domain.DataSource;
import com.ocare.health.domain.HealthEntry;
import com.ocare.health.domain.HealthRecord;
import com.ocare.health.dto.HealthDataRequest;
import com.ocare.health.repository.DataSourceRepository;
import com.ocare.health.repository.HealthEntryRepository;
import com.ocare.health.repository.HealthRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
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
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // ISO 8601 형식을 먼저 파싱
            return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
        } catch (Exception e) {
            // 기본 형식 시도
            return LocalDateTime.parse(dateTimeStr, FORMATTER);
        }
    }

    @Transactional
    public void loadJsonData(String fileName, Long userId) throws IOException {
        ClassPathResource resource = new ClassPathResource("json/" + fileName);
        HealthDataRequest request = objectMapper.readValue(resource.getInputStream(), HealthDataRequest.class);

        // 1. HealthRecord 저장
        HealthRecord healthRecord;
        if (healthRecordRepository.existsByRecordKey(request.getRecordkey())) {
            healthRecord = healthRecordRepository.findByRecordKey(request.getRecordkey())
                    .orElseThrow(() -> new IllegalStateException("Record not found"));
            log.info("기존 레코드 사용: {}", request.getRecordkey());
        } else {
            healthRecord = HealthRecord.builder()
                    .userId(userId)
                    .recordKey(request.getRecordkey())
                    .build();
            healthRecord = healthRecordRepository.save(healthRecord);
            log.info("새 레코드 생성: {}", request.getRecordkey());
        }

        // 2. DataSource 저장 또는 업데이트
        if (request.getData().getSource() != null) {
            HealthDataRequest.Source source = request.getData().getSource();
            
            DataSource dataSource = dataSourceRepository.findByRecordId(healthRecord.getId())
                    .orElse(DataSource.builder()
                            .recordId(healthRecord.getId())
                            .build());
            
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

        // 3. HealthEntry 저장
        List<HealthEntry> entries = new ArrayList<>();
        for (HealthDataRequest.Entry entry : request.getData().getEntries()) {
            HealthEntry healthEntry = HealthEntry.builder()
                    .userId(userId)
                    .recordId(healthRecord.getId())
                    .periodFrom(parseDateTime(entry.getPeriod().getFrom()))
                    .periodTo(parseDateTime(entry.getPeriod().getTo()))
                    .steps(BigDecimal.valueOf(entry.getStepsAsDouble()))
                    .distanceValue(BigDecimal.valueOf(entry.getDistance().getValue()))
                    .distanceUnit(entry.getDistance().getUnit())
                    .caloriesValue(BigDecimal.valueOf(entry.getCalories().getValue()))
                    .caloriesUnit(entry.getCalories().getUnit())
                    .build();
            entries.add(healthEntry);
        }

        healthEntryRepository.saveAll(entries);
        log.info("{}개의 엔트리 저장 완료", entries.size());

        // 4. Daily/Monthly 집계 업데이트
        healthSummaryService.aggregateSummaries(request.getRecordkey(), entries);
        
        // 5. 캐시 무효화 (새 데이터 추가되었으므로)
        redisCacheService.invalidateUserCache(userId);
        redisCacheService.invalidateSummaryCache(request.getRecordkey());
    }

    @Transactional(readOnly = true)
    public List<HealthEntry> getHealthEntriesByUserId(Long userId) {
        // 1. Redis 캐시 조회
        List<HealthEntry> cached = redisCacheService.getHealthEntries(userId);
        if (cached != null) {
            return cached;
        }

        // 2. DB 조회
        List<HealthEntry> entries = healthEntryRepository.findByUserId(userId);
        
        // 3. Redis 캐시 저장
        if (!entries.isEmpty()) {
            redisCacheService.cacheHealthEntries(userId, entries);
        }

        return entries;
    }
}
