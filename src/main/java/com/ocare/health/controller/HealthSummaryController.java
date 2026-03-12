package com.ocare.health.controller;

import com.ocare.health.domain.DailyHealthSummary;
import com.ocare.health.domain.MonthlyHealthSummary;
import com.ocare.health.service.HealthSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health/summary")
@RequiredArgsConstructor
public class HealthSummaryController {

    private final HealthSummaryService healthSummaryService;

    @GetMapping("/daily/{recordKey}")
    public ResponseEntity<List<DailyHealthSummary>> getDailySummary(@PathVariable String recordKey) {
        List<DailyHealthSummary> summaries = healthSummaryService.getDailySummary(recordKey);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/monthly/{recordKey}")
    public ResponseEntity<List<MonthlyHealthSummary>> getMonthlySummary(@PathVariable String recordKey) {
        List<MonthlyHealthSummary> summaries = healthSummaryService.getMonthlySummary(recordKey);
        return ResponseEntity.ok(summaries);
    }
}
