package com.hospitalmanagement.repository;

import com.hospitalmanagement.model.InvoiceLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItem, Long> {
}
