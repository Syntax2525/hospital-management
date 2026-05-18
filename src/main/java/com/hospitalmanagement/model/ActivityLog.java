package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "activity_logs")
public class ActivityLog extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 120)
    private String action;

    @Column(length = 1000)
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private StaffUser actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    public ActivityLog() {
    }

    public ActivityLog(String action, String details, StaffUser actor, Patient patient) {
        this.action = action;
        this.details = details;
        this.actor = actor;
        this.patient = patient;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public StaffUser getActor() {
        return actor;
    }

    public void setActor(StaffUser actor) {
        this.actor = actor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

}
