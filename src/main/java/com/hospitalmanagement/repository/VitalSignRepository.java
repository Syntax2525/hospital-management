package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
}
