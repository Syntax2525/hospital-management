package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
    Optional<Allergy> findByNameIgnoreCase(String name);
}
