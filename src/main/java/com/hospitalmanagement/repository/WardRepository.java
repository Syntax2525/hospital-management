package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WardRepository extends JpaRepository<Ward, Long> {
    Optional<Ward> findByNameIgnoreCase(String name);
}
