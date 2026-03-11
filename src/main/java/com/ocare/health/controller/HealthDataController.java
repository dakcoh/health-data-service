package com.ocare.health.controller;

import com.ocare.health.domain.HealthEntry;
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

    @PostMapping("/load/{fileName}")
    public ResponseEntity<String> loadJsonData(
            @PathVariable String fileName,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        try {
            healthDataService.loadJsonData(fileName, userId);
            return ResponseEntity.ok(fileName + " 데이터 로드 완료");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 로드 실패: " + e.getMessage());
        }
    }

    @GetMapping("/entries")
    public ResponseEntity<?> getHealthEntries(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        List<HealthEntry> entries = healthDataService.getHealthEntriesByUserId(userId);
        return ResponseEntity.ok(entries);
    }
}
