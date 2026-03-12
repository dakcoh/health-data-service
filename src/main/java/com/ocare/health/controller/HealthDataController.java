package com.ocare.health.controller;

import com.ocare.health.domain.HealthEntry;
import com.ocare.health.exception.UnauthorizedException;
import com.ocare.health.service.HealthDataService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthDataController {

    private final HealthDataService healthDataService;

    private Long getUserIdFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("로그인이 필요합니다");
        }
        return userId;
    }

    @PostMapping("/load/{fileName}")
    public ResponseEntity<String> loadJsonData(
            @PathVariable String fileName,
            HttpSession session) throws IOException {
        Long userId = getUserIdFromSession(session);
        healthDataService.loadJsonData(fileName, userId);
        return ResponseEntity.ok(fileName + " 데이터 로드 완료");
    }

    @GetMapping("/entries")
    public ResponseEntity<List<HealthEntry>> getHealthEntries(HttpSession session) {
        Long userId = getUserIdFromSession(session);
        List<HealthEntry> entries = healthDataService.getHealthEntriesByUserId(userId);
        return ResponseEntity.ok(entries);
    }
}
