package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.Bed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BedRepository extends JpaRepository<Bed, Long> {
    Optional<Bed> findByBedNumber(String bedNumber);
}
