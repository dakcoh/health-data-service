package com.ocare.health.controller;

import com.ocare.health.dto.ApiResponse;
import com.ocare.health.dto.DailySummaryDto;
import com.ocare.health.dto.MonthlySummaryDto;
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
    public ResponseEntity<ApiResponse<List<DailySummaryDto>>> getDailySummary(@PathVariable String recordKey) {
        List<DailySummaryDto> summaries = healthSummaryService.getDailySummary(recordKey)
                .stream()
                .map(DailySummaryDto::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("일별 집계 조회 완료", summaries));
    }

    @GetMapping("/monthly/{recordKey}")
    public ResponseEntity<ApiResponse<List<MonthlySummaryDto>>> getMonthlySummary(@PathVariable String recordKey) {
        List<MonthlySummaryDto> summaries = healthSummaryService.getMonthlySummary(recordKey)
                .stream()
                .map(MonthlySummaryDto::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("월별 집계 조회 완료", summaries));
    }
}
