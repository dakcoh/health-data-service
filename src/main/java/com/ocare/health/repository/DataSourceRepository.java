package com.ocare.health.repository;

import com.ocare.health.domain.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {
    Optional<DataSource> findByRecordId(Long recordId);
}
