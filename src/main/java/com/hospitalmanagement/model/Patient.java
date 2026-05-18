package com.hospitalmanagement.model;

import com.hospitalmanagement.enums.BloodGroup;
import com.hospitalmanagement.enums.Gender;
import com.hospitalmanagement.enums.PatientPriority;
import com.hospitalmanagement.enums.PatientStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "patients")
public class Patient extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 30)
    private String patientNumber;

    @NotBlank
    @Column(nullable = false, length = 140)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Min(0)
    @Max(130)
    private Integer age;

    @Column(length = 40)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 120)
    private String insuranceProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PatientPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PatientStatus status;

    private LocalDate lastVisitDate;

    @Column(length = 500)
    private String photoUrl;

    @ManyToMany
    @JoinTable(
            name = "patient_allergies",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    private Set<Allergy> allergies = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "patient")
    private List<Encounter> encounters = new ArrayList<>();
    public Patient() {
    }

    public Patient(String patientNumber, String fullName, Gender gender, Integer age, String phone, String address, String insuranceProvider, BloodGroup bloodGroup, PatientPriority priority, PatientStatus status, LocalDate lastVisitDate, String photoUrl, Set<Allergy> allergies, List<PatientDocument> documents, List<Appointment> appointments, List<Encounter> encounters) {
        this.patientNumber = patientNumber;
        this.fullName = fullName;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.address = address;
        this.insuranceProvider = insuranceProvider;
        this.bloodGroup = bloodGroup;
        this.priority = priority;
        this.status = status;
        this.lastVisitDate = lastVisitDate;
        this.photoUrl = photoUrl;
        this.allergies = allergies;
        this.documents = documents;
        this.appointments = appointments;
        this.encounters = encounters;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public PatientPriority getPriority() {
        return priority;
    }

    public void setPriority(PatientPriority priority) {
        this.priority = priority;
    }

    public PatientStatus getStatus() {
        return status;
    }

    public void setStatus(PatientStatus status) {
        this.status = status;
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Set<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(Set<Allergy> allergies) {
        this.allergies = allergies;
    }

    public List<PatientDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PatientDocument> documents) {
        this.documents = documents;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public List<Encounter> getEncounters() {
        return encounters;
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters;
    }

}
