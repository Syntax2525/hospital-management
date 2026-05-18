package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    Optional<LabTest> findByNameIgnoreCase(String name);
}
