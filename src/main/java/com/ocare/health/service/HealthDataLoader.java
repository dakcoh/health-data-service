package com.ocare.health.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocare.health.dto.HealthDataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthDataLoader {

    private final ObjectMapper objectMapper;

    public HealthDataRequest loadFromFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("json/" + fileName);
        HealthDataRequest request = objectMapper.readValue(resource.getInputStream(), HealthDataRequest.class);
        log.info("JSON 파일 로드 완료: {}, 엔트리 수: {}", fileName, request.getData().getEntries().size());
        return request;
    }
}
