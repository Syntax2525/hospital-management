package com.hospitalmanagement.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "consultations")
public class Consultation extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime consultedAt;

    @Column(length = 5000)
    private String clinicalNotes;

    @Column(length = 2000)
    private String treatmentPlan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false, unique = true)
    private Encounter encounter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private StaffUser doctor;

    @ManyToMany
    @JoinTable(
            name = "consultation_diagnoses",
            joinColumns = @JoinColumn(name = "consultation_id"),
            inverseJoinColumns = @JoinColumn(name = "diagnosis_code_id")
    )
    private Set<DiagnosisCode> diagnoses = new HashSet<>();

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabOrder> labOrders = new ArrayList<>();

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions = new ArrayList<>();
    public Consultation() {
    }

    public Consultation(LocalDateTime consultedAt, String clinicalNotes, String treatmentPlan, Encounter encounter, Patient patient, StaffUser doctor, Set<DiagnosisCode> diagnoses, List<LabOrder> labOrders, List<Prescription> prescriptions) {
        this.consultedAt = consultedAt;
        this.clinicalNotes = clinicalNotes;
        this.treatmentPlan = treatmentPlan;
        this.encounter = encounter;
        this.patient = patient;
        this.doctor = doctor;
        this.diagnoses = diagnoses;
        this.labOrders = labOrders;
        this.prescriptions = prescriptions;
    }

    public LocalDateTime getConsultedAt() {
        return consultedAt;
    }

    public void setConsultedAt(LocalDateTime consultedAt) {
        this.consultedAt = consultedAt;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
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

    public StaffUser getDoctor() {
        return doctor;
    }

    public void setDoctor(StaffUser doctor) {
        this.doctor = doctor;
    }

    public Set<DiagnosisCode> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(Set<DiagnosisCode> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public List<LabOrder> getLabOrders() {
        return labOrders;
    }

    public void setLabOrders(List<LabOrder> labOrders) {
        this.labOrders = labOrders;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

}
