package com.hospitalmanagement.config;

import com.hospitalmanagement.enums.*;
import com.hospitalmanagement.model.*;
import com.hospitalmanagement.repository.*;
import com.hospitalmanagement.util.NumberGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            DepartmentRepository departments,
            StaffUserRepository staffUsers,
            PatientRepository patients,
            AllergyRepository allergies,
            LabTestRepository labTests,
            MedicationRepository medications,
            WardRepository wards,
            BedRepository beds,
            NotificationRepository notifications,
            PasswordEncoder passwordEncoder,
            @Value("${hms.bootstrap.admin-email}") String adminEmail,
            @Value("${hms.bootstrap.admin-password}") String adminPassword
    ) {
        return args -> {
            Department general = departments.findByCode("GEN").orElseGet(() -> {
                Department department = new Department();
                department.setName("General Medicine");
                department.setCode("GEN");
                department.setDescription("Primary clinical services");
                return departments.save(department);
            });
            departments.findByCode("LAB").orElseGet(() -> {
                Department department = new Department();
                department.setName("Laboratory");
                department.setCode("LAB");
                return departments.save(department);
            });
            departments.findByCode("PHARM").orElseGet(() -> {
                Department department = new Department();
                department.setName("Pharmacy");
                department.setCode("PHARM");
                return departments.save(department);
            });

            staffUsers.findByEmailIgnoreCase(adminEmail).orElseGet(() -> {
                StaffUser user = new StaffUser();
                user.setFullName("System Administrator");
                user.setEmail(adminEmail);
                user.setPasswordHash(passwordEncoder.encode(adminPassword));
                user.setRole(UserRole.ADMIN);
                user.setStatus(AccountStatus.ACTIVE);
                user.setDepartment(general);
                user.setPhone("+1 (555) 010-0001");
                return staffUsers.save(user);
            });
            staffUsers.findByEmailIgnoreCase("doctor@mediflow.local").orElseGet(() -> {
                StaffUser user = new StaffUser();
                user.setFullName("Dr. Sarah Chen");
                user.setEmail("doctor@mediflow.local");
                user.setPasswordHash(passwordEncoder.encode("doctor123"));
                user.setRole(UserRole.DOCTOR);
                user.setStatus(AccountStatus.ACTIVE);
                user.setDepartment(general);
                return staffUsers.save(user);
            });
            seedUser(staffUsers, passwordEncoder, general, "Nurse Amina Yusuf", "nurse@mediflow.local", "nurse123", UserRole.NURSE);
            seedUser(staffUsers, passwordEncoder, general, "Liam Carter", "lab@mediflow.local", "labtech123", UserRole.LAB_TECHNICIAN);
            seedUser(staffUsers, passwordEncoder, general, "Priya Shah", "pharmacy@mediflow.local", "pharmacist123", UserRole.PHARMACIST);
            seedUser(staffUsers, passwordEncoder, general, "Grace Mwinyi", "billing@mediflow.local", "billing123", UserRole.BILLING);
            seedUser(staffUsers, passwordEncoder, general, "Noah Reed", "reception@mediflow.local", "reception123", UserRole.RECEPTIONIST);

            seedAllergy(allergies, "Penicillin");
            seedAllergy(allergies, "Latex");
            seedPatient(patients, allergies, "PT-88291", "Jonathan Miller", Gender.MALE, 42, "+1 (555) 010-2291", "Penicillin", PatientPriority.HIGH);
            seedPatient(patients, allergies, "PT-77402", "Maria Garcia", Gender.FEMALE, 35, "+1 (555) 010-7402", null, PatientPriority.STABLE);

            seedLab(labTests, "Complete Blood Count (CBC)", "CBC", BigDecimal.valueOf(35));
            seedLab(labTests, "Lipid profile", "LIPID", BigDecimal.valueOf(55));
            seedLab(labTests, "Chest X-Ray (AP)", "XR-CHEST", BigDecimal.valueOf(80));
            seedLab(labTests, "ECG", "ECG", BigDecimal.valueOf(45));

            seedMedication(medications, "Amoxicillin", "500mg", "Capsule", 80, BigDecimal.valueOf(3.50));
            seedMedication(medications, "Paracetamol", "500mg", "Tablet", 180, BigDecimal.valueOf(1.25));
            seedMedication(medications, "Metformin", "850mg", "Tablet", 18, BigDecimal.valueOf(2.10));

            Ward ward = wards.findByNameIgnoreCase("Ward 4").orElseGet(() -> {
                Ward newWard = new Ward();
                newWard.setName("Ward 4");
                newWard.setFloor("2");
                return wards.save(newWard);
            });
            for (int i = 1; i <= 6; i++) {
                String bedNumber = "W4-" + i;
                int index = i;
                beds.findByBedNumber(bedNumber).orElseGet(() -> {
                    Bed bed = new Bed();
                    bed.setBedNumber(bedNumber);
                    bed.setWard(ward);
                    bed.setStatus(index <= 4 ? BedStatus.AVAILABLE : BedStatus.OCCUPIED);
                    return beds.save(bed);
                });
            }

            if (notifications.count() == 0) {
                Notification lowStock = new Notification();
                lowStock.setTitle("Low stock alert");
                lowStock.setMessage("Metformin is near reorder level.");
                lowStock.setType(NotificationType.LOW_STOCK);
                lowStock.setStatus(NotificationStatus.UNREAD);
                notifications.save(lowStock);

                Notification triage = new Notification();
                triage.setTitle("Triage queue updated");
                triage.setMessage("Emergency triage requires clinician review.");
                triage.setType(NotificationType.TRIAGE_ALERT);
                triage.setStatus(NotificationStatus.UNREAD);
                notifications.save(triage);
            }
        };
    }

    private void seedAllergy(AllergyRepository allergies, String name) {
        allergies.findByNameIgnoreCase(name).orElseGet(() -> {
            Allergy allergy = new Allergy();
            allergy.setName(name);
            return allergies.save(allergy);
        });
    }

    private void seedPatient(PatientRepository patients, AllergyRepository allergies, String number, String name, Gender gender,
                             int age, String phone, String allergyName, PatientPriority priority) {
        patients.findByPatientNumber(number).orElseGet(() -> {
            Patient patient = new Patient();
            patient.setPatientNumber(number);
            patient.setFullName(name);
            patient.setGender(gender);
            patient.setAge(age);
            patient.setPhone(phone);
            patient.setAddress("Main Street");
            patient.setInsuranceProvider("General Insurance");
            patient.setBloodGroup(BloodGroup.UNKNOWN);
            patient.setPriority(priority);
            patient.setStatus(PatientStatus.ACTIVE);
            patient.setLastVisitDate(LocalDate.now());
            if (allergyName != null) {
                allergies.findByNameIgnoreCase(allergyName).ifPresent(patient.getAllergies()::add);
            }
            return patients.save(patient);
        });
    }

    private void seedLab(LabTestRepository labTests, String name, String code, BigDecimal price) {
        labTests.findByNameIgnoreCase(name).orElseGet(() -> {
            LabTest labTest = new LabTest();
            labTest.setName(name);
            labTest.setCode(code);
            labTest.setCategory("General");
            labTest.setPrice(price);
            return labTests.save(labTest);
        });
    }

    private void seedMedication(MedicationRepository medications, String name, String strength, String form, int stock, BigDecimal price) {
        medications.findByNameIgnoreCase(name).orElseGet(() -> {
            Medication medication = new Medication();
            medication.setName(name);
            medication.setStrength(strength);
            medication.setDosageForm(form);
            medication.setStockQuantity(stock);
            medication.setReorderLevel(20);
            medication.setUnitPrice(price);
            medication.setInventoryStatus(stock <= 20 ? InventoryStatus.LOW_STOCK : InventoryStatus.IN_STOCK);
            return medications.save(medication);
        });
    }

    private void seedUser(StaffUserRepository staffUsers, PasswordEncoder passwordEncoder, Department department,
                          String fullName, String email, String password, UserRole role) {
        staffUsers.findByEmailIgnoreCase(email).orElseGet(() -> {
            StaffUser user = new StaffUser();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setRole(role);
            user.setStatus(AccountStatus.ACTIVE);
            user.setDepartment(department);
            return staffUsers.save(user);
        });
    }
}
