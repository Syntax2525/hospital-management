package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.LabOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabOrderItemRepository extends JpaRepository<LabOrderItem, Long> {
}
