package com.ocare.health.service;

import com.ocare.health.domain.DailyHealthSummary;
import com.ocare.health.domain.HealthEntry;
import com.ocare.health.domain.MonthlyHealthSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String ENTRIES_KEY_PREFIX = "health:entries:user:";
    private static final String DAILY_SUMMARY_KEY_PREFIX = "health:summary:daily:";
    private static final String MONTHLY_SUMMARY_KEY_PREFIX = "health:summary:monthly:";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    /**
     * 사용자 건강 데이터 캐시 조회
     */
    @SuppressWarnings("unchecked")
    public List<HealthEntry> getHealthEntries(Long userId) {
        String key = ENTRIES_KEY_PREFIX + userId;
        Object cached = redisTemplate.opsForValue().get(key);
        
        if (cached != null) {
            log.info("Redis 캐시 HIT: userId={}", userId);
            return (List<HealthEntry>) cached;
        }
        
        log.info("Redis 캐시 MISS: userId={}", userId);
        return null;
    }

    /**
     * 사용자 건강 데이터 캐시 저장
     */
    public void cacheHealthEntries(Long userId, List<HealthEntry> entries) {
        String key = ENTRIES_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, entries, CACHE_TTL);
        log.info("Redis 캐시 저장: userId={}, 데이터 수={}, TTL={}시간", userId, entries.size(), CACHE_TTL.toHours());
    }

    /**
     * 일별 집계 캐시 조회
     */
    @SuppressWarnings("unchecked")
    public List<DailyHealthSummary> getDailySummary(String recordKey) {
        String key = DAILY_SUMMARY_KEY_PREFIX + recordKey;
        Object cached = redisTemplate.opsForValue().get(key);
        
        if (cached != null) {
            log.info("Redis 캐시 HIT: recordKey={} (daily)", recordKey);
            return (List<DailyHealthSummary>) cached;
        }
        
        log.info("Redis 캐시 MISS: recordKey={} (daily)", recordKey);
        return null;
    }

    /**
     * 일별 집계 캐시 저장
     */
    public void cacheDailySummary(String recordKey, List<DailyHealthSummary> summaries) {
        String key = DAILY_SUMMARY_KEY_PREFIX + recordKey;
        redisTemplate.opsForValue().set(key, summaries, CACHE_TTL);
        log.info("Redis 캐시 저장: recordKey={} (daily), 데이터 수={}", recordKey, summaries.size());
    }

    /**
     * 월별 집계 캐시 조회
     */
    @SuppressWarnings("unchecked")
    public List<MonthlyHealthSummary> getMonthlySummary(String recordKey) {
        String key = MONTHLY_SUMMARY_KEY_PREFIX + recordKey;
        Object cached = redisTemplate.opsForValue().get(key);
        
        if (cached != null) {
            log.info("Redis 캐시 HIT: recordKey={} (monthly)", recordKey);
            return (List<MonthlyHealthSummary>) cached;
        }
        
        log.info("Redis 캐시 MISS: recordKey={} (monthly)", recordKey);
        return null;
    }

    /**
     * 월별 집계 캐시 저장
     */
    public void cacheMonthlySummary(String recordKey, List<MonthlyHealthSummary> summaries) {
        String key = MONTHLY_SUMMARY_KEY_PREFIX + recordKey;
        redisTemplate.opsForValue().set(key, summaries, CACHE_TTL);
        log.info("Redis 캐시 저장: recordKey={} (monthly), 데이터 수={}", recordKey, summaries.size());
    }

    /**
     * 특정 사용자의 캐시 무효화
     */
    public void invalidateUserCache(Long userId) {
        String key = ENTRIES_KEY_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("Redis 캐시 무효화: userId={}", userId);
    }

    /**
     * 특정 recordKey의 집계 캐시 무효화
     */
    public void invalidateSummaryCache(String recordKey) {
        redisTemplate.delete(DAILY_SUMMARY_KEY_PREFIX + recordKey);
        redisTemplate.delete(MONTHLY_SUMMARY_KEY_PREFIX + recordKey);
        log.info("Redis 캐시 무효화: recordKey={} (daily, monthly)", recordKey);
    }
}
