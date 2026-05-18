package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "patient_documents")
public class PatientDocument extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 160)
    private String fileName;

    @Column(nullable = false, length = 80)
    private String contentType;

    @Column(nullable = false, length = 500)
    private String fileUrl;

    @Column(length = 80)
    private String documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    public PatientDocument() {
    }

    public PatientDocument(String fileName, String contentType, String fileUrl, String documentType, Patient patient) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileUrl = fileUrl;
        this.documentType = documentType;
        this.patient = patient;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

}
