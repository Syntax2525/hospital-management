package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.TriageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriageRecordRepository extends JpaRepository<TriageRecord, Long> {
}
