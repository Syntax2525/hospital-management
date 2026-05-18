package com.hospitalmanagement.model;

import com.hospitalmanagement.enums.LabOrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lab_orders")
public class LabOrder extends BaseEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String orderNumber;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LabOrderStatus status;

    @Column(length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id")
    private StaffUser requestedBy;

    @OneToMany(mappedBy = "labOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabOrderItem> items = new ArrayList<>();
    public LabOrder() {
    }

    public LabOrder(String orderNumber, LocalDateTime requestedAt, LabOrderStatus status, String notes, Patient patient, Consultation consultation, StaffUser requestedBy, List<LabOrderItem> items) {
        this.orderNumber = orderNumber;
        this.requestedAt = requestedAt;
        this.status = status;
        this.notes = notes;
        this.patient = patient;
        this.consultation = consultation;
        this.requestedBy = requestedBy;
        this.items = items;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LabOrderStatus getStatus() {
        return status;
    }

    public void setStatus(LabOrderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public StaffUser getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(StaffUser requestedBy) {
        this.requestedBy = requestedBy;
    }

    public List<LabOrderItem> getItems() {
        return items;
    }

    public void setItems(List<LabOrderItem> items) {
        this.items = items;
    }

}
