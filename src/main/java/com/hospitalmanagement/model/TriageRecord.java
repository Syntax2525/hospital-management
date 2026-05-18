package com.hospitalmanagement.model;

import com.hospitalmanagement.enums.PatientPriority;
import com.hospitalmanagement.enums.TriageCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "triage_records")
public class TriageRecord extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String chiefComplaint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TriageCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PatientPriority priority;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false, unique = true)
    private Encounter encounter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id")
    private StaffUser nurse;

    @OneToOne(mappedBy = "triageRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private VitalSign vitalSign;
    public TriageRecord() {
    }

    public TriageRecord(LocalDateTime recordedAt, String chiefComplaint, TriageCategory category, PatientPriority priority, Encounter encounter, Patient patient, StaffUser nurse, VitalSign vitalSign) {
        this.recordedAt = recordedAt;
        this.chiefComplaint = chiefComplaint;
        this.category = category;
        this.priority = priority;
        this.encounter = encounter;
        this.patient = patient;
        this.nurse = nurse;
        this.vitalSign = vitalSign;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public TriageCategory getCategory() {
        return category;
    }

    public void setCategory(TriageCategory category) {
        this.category = category;
    }

    public PatientPriority getPriority() {
        return priority;
    }

    public void setPriority(PatientPriority priority) {
        this.priority = priority;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public StaffUser getNurse() {
        return nurse;
    }

    public void setNurse(StaffUser nurse) {
        this.nurse = nurse;
    }

    public VitalSign getVitalSign() {
        return vitalSign;
    }

    public void setVitalSign(VitalSign vitalSign) {
        this.vitalSign = vitalSign;
    }

}
