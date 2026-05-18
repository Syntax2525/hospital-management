package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.Admission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
}
