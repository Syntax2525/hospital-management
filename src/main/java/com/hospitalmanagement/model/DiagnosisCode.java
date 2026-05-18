package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "diagnosis_codes")
public class DiagnosisCode extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String description;

    @ManyToMany(mappedBy = "diagnoses")
    private Set<Consultation> consultations = new HashSet<>();
    public DiagnosisCode() {
    }

    public DiagnosisCode(String code, String description, Set<Consultation> consultations) {
        this.code = code;
        this.description = description;
        this.consultations = consultations;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(Set<Consultation> consultations) {
        this.consultations = consultations;
    }

}
