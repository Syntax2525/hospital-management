package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {
    Optional<LabOrder> findByOrderNumber(String orderNumber);
}
