package com.ocare.health.repository;

import com.ocare.health.domain.HealthEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthEntryRepository extends JpaRepository<HealthEntry, Long> {
    List<HealthEntry> findByUserId(Long userId);
}
