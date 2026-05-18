package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.PatientDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientDocumentRepository extends JpaRepository<PatientDocument, Long> {
}
