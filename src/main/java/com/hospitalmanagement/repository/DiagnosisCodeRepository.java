package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.DiagnosisCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiagnosisCodeRepository extends JpaRepository<DiagnosisCode, Long> {
    Optional<DiagnosisCode> findByCode(String code);
}
