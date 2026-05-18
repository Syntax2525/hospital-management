package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    Optional<Encounter> findByEncounterNumber(String encounterNumber);
}
