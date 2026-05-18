package com.hospitalmanagement.model;

import com.hospitalmanagement.enums.DispenseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Table(name = "pharmacy_dispenses")
public class PharmacyDispense extends BaseEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String dispenseNumber;

    @Min(1)
    private Integer quantityDispensed;

    private LocalDateTime dispensedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DispenseStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_item_id", nullable = false, unique = true)
    private PrescriptionItem prescriptionItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensed_by_id")
    private StaffUser dispensedBy;
    public PharmacyDispense() {
    }

    public PharmacyDispense(String dispenseNumber, Integer quantityDispensed, LocalDateTime dispensedAt, DispenseStatus status, PrescriptionItem prescriptionItem, StaffUser dispensedBy) {
        this.dispenseNumber = dispenseNumber;
        this.quantityDispensed = quantityDispensed;
        this.dispensedAt = dispensedAt;
        this.status = status;
        this.prescriptionItem = prescriptionItem;
        this.dispensedBy = dispensedBy;
    }

    public String getDispenseNumber() {
        return dispenseNumber;
    }

    public void setDispenseNumber(String dispenseNumber) {
        this.dispenseNumber = dispenseNumber;
    }

    public Integer getQuantityDispensed() {
        return quantityDispensed;
    }

    public void setQuantityDispensed(Integer quantityDispensed) {
        this.quantityDispensed = quantityDispensed;
    }

    public LocalDateTime getDispensedAt() {
        return dispensedAt;
    }

    public void setDispensedAt(LocalDateTime dispensedAt) {
        this.dispensedAt = dispensedAt;
    }

    public DispenseStatus getStatus() {
        return status;
    }

    public void setStatus(DispenseStatus status) {
        this.status = status;
    }

    public PrescriptionItem getPrescriptionItem() {
        return prescriptionItem;
    }

    public void setPrescriptionItem(PrescriptionItem prescriptionItem) {
        this.prescriptionItem = prescriptionItem;
    }

    public StaffUser getDispensedBy() {
        return dispensedBy;
    }

    public void setDispensedBy(StaffUser dispensedBy) {
        this.dispensedBy = dispensedBy;
    }

}
