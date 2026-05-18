package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.PharmacyDispense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacyDispenseRepository extends JpaRepository<PharmacyDispense, Long> {
    Optional<PharmacyDispense> findByDispenseNumber(String dispenseNumber);
}
