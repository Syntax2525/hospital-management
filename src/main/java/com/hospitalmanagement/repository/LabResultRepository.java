package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {
}
