package com.hospitalmanagement.model;

import com.hospitalmanagement.enums.EncounterStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "encounters")
public class Encounter extends BaseEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String encounterNumber;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EncounterStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_clinician_id")
    private StaffUser primaryClinician;

    @OneToOne(mappedBy = "encounter", cascade = CascadeType.ALL)
    private TriageRecord triageRecord;

    @OneToOne(mappedBy = "encounter", cascade = CascadeType.ALL)
    private Consultation consultation;

    @OneToOne(mappedBy = "encounter")
    private Invoice invoice;

    @OneToMany(mappedBy = "encounter")
    private List<Admission> admissions = new ArrayList<>();
    public Encounter() {
    }

    public Encounter(String encounterNumber, LocalDateTime startedAt, LocalDateTime endedAt, EncounterStatus status, Patient patient, StaffUser primaryClinician, TriageRecord triageRecord, Consultation consultation, Invoice invoice, List<Admission> admissions) {
        this.encounterNumber = encounterNumber;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
        this.patient = patient;
        this.primaryClinician = primaryClinician;
        this.triageRecord = triageRecord;
        this.consultation = consultation;
        this.invoice = invoice;
        this.admissions = admissions;
    }

    public String getEncounterNumber() {
        return encounterNumber;
    }

    public void setEncounterNumber(String encounterNumber) {
        this.encounterNumber = encounterNumber;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public EncounterStatus getStatus() {
        return status;
    }

    public void setStatus(EncounterStatus status) {
        this.status = status;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public StaffUser getPrimaryClinician() {
        return primaryClinician;
    }

    public void setPrimaryClinician(StaffUser primaryClinician) {
        this.primaryClinician = primaryClinician;
    }

    public TriageRecord getTriageRecord() {
        return triageRecord;
    }

    public void setTriageRecord(TriageRecord triageRecord) {
        this.triageRecord = triageRecord;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public List<Admission> getAdmissions() {
        return admissions;
    }

    public void setAdmissions(List<Admission> admissions) {
        this.admissions = admissions;
    }

}
