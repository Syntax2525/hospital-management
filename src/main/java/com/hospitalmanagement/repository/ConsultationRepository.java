package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}
