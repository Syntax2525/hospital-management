package com.hospitalmanagement.service.impl;
import com.hospitalmanagement.dto.*;
import com.hospitalmanagement.enums.*;
import com.hospitalmanagement.exception.BusinessRuleException;
import com.hospitalmanagement.exception.ResourceNotFoundException;
import com.hospitalmanagement.mapper.HospitalMapper;
import com.hospitalmanagement.model.*;
import com.hospitalmanagement.repository.ActivityLogRepository;
import com.hospitalmanagement.repository.AllergyRepository;
import com.hospitalmanagement.repository.AppointmentRepository;
import com.hospitalmanagement.repository.BedRepository;
import com.hospitalmanagement.repository.ConsultationRepository;
import com.hospitalmanagement.repository.DepartmentRepository;
import com.hospitalmanagement.repository.DiagnosisCodeRepository;
import com.hospitalmanagement.repository.EncounterRepository;
import com.hospitalmanagement.repository.InvoiceRepository;
import com.hospitalmanagement.repository.LabOrderItemRepository;
import com.hospitalmanagement.repository.LabOrderRepository;
import com.hospitalmanagement.repository.LabResultRepository;
import com.hospitalmanagement.repository.LabTestRepository;
import com.hospitalmanagement.repository.MedicationRepository;
import com.hospitalmanagement.repository.NotificationRepository;
import com.hospitalmanagement.repository.PatientRepository;
import com.hospitalmanagement.repository.PaymentRepository;
import com.hospitalmanagement.repository.PharmacyDispenseRepository;
import com.hospitalmanagement.repository.PrescriptionItemRepository;
import com.hospitalmanagement.repository.PrescriptionRepository;
import com.hospitalmanagement.repository.StaffUserRepository;
import com.hospitalmanagement.repository.TriageRecordRepository;
import com.hospitalmanagement.repository.WardRepository;
import com.hospitalmanagement.security.TokenService;
import com.hospitalmanagement.service.HospitalService;
import com.hospitalmanagement.util.NumberGenerator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class HospitalServiceImpl implements HospitalService {
    private final PatientRepository patients;
    private final ActivityLogRepository activityLogs;
    private final AllergyRepository allergies;
    private final StaffUserRepository staffUsers;
    private final DepartmentRepository departments;
    private final EncounterRepository encounters;
    private final TriageRecordRepository triageRecords;
    private final ConsultationRepository consultations;
    private final DiagnosisCodeRepository diagnosisCodes;
    private final LabOrderRepository labOrders;
    private final LabOrderItemRepository labOrderItems;
    private final LabTestRepository labTests;
    private final LabResultRepository labResults;
    private final InvoiceRepository invoices;
    private final PaymentRepository payments;
    private final MedicationRepository medications;
    private final PrescriptionRepository prescriptions;
    private final PrescriptionItemRepository prescriptionItems;
    private final PharmacyDispenseRepository dispenses;
    private final NotificationRepository notifications;
    private final AppointmentRepository appointments;
    private final BedRepository beds;
    private final WardRepository wards;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final HospitalMapper mapper;

    public HospitalServiceImpl(
            PatientRepository patients,
            ActivityLogRepository activityLogs,
            AllergyRepository allergies,
            StaffUserRepository staffUsers,
            DepartmentRepository departments,
            EncounterRepository encounters,
            TriageRecordRepository triageRecords,
            ConsultationRepository consultations,
            DiagnosisCodeRepository diagnosisCodes,
            LabOrderRepository labOrders,
            LabOrderItemRepository labOrderItems,
            LabTestRepository labTests,
            LabResultRepository labResults,
            InvoiceRepository invoices,
            PaymentRepository payments,
            MedicationRepository medications,
            PrescriptionRepository prescriptions,
            PrescriptionItemRepository prescriptionItems,
            PharmacyDispenseRepository dispenses,
            NotificationRepository notifications,
            AppointmentRepository appointments,
            BedRepository beds,
            WardRepository wards,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            HospitalMapper mapper
    ) {
        this.patients = patients;
        this.activityLogs = activityLogs;
        this.allergies = allergies;
        this.staffUsers = staffUsers;
        this.departments = departments;
        this.encounters = encounters;
        this.triageRecords = triageRecords;
        this.consultations = consultations;
        this.diagnosisCodes = diagnosisCodes;
        this.labOrders = labOrders;
        this.labOrderItems = labOrderItems;
        this.labTests = labTests;
        this.labResults = labResults;
        this.invoices = invoices;
        this.payments = payments;
        this.medications = medications;
        this.prescriptions = prescriptions;
        this.prescriptionItems = prescriptionItems;
        this.dispenses = dispenses;
        this.notifications = notifications;
        this.appointments = appointments;
        this.beds = beds;
        this.wards = wards;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

    public AuthSessionResponse login(AuthLoginRequest request) {
        StaffUser user = staffUsers.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (user.getStatus() != AccountStatus.ACTIVE || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new AuthSessionResponse(true, user.getFullName(), user.getRole(), user.getEmail(), tokenService.issue(user), dashboardUrl(user.getRole()));
    }

    @Transactional(readOnly = true)
    public List<PatientSummaryResponse> listPatients() {
        StaffUser actor = currentUser();
        return patients.findAll().stream()
                .filter(patient -> canViewPatient(actor, patient))
                .map(mapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PatientDetailResponse> listPatientDetails() {
        StaffUser actor = currentUser();
        return patients.findAll().stream()
                .filter(patient -> canViewPatient(actor, patient))
                .map(mapper::toDetail)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientDetailResponse getPatient(Long id) {
        Patient patient = patient(id);
        requirePatientAccess(currentUser(), patient);
        return mapper.toDetail(patient);
    }

    public PatientDetailResponse registerPatient(PatientRegistrationRequest request) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.RECEPTIONIST);
        Patient patient = new Patient();
        patient.setPatientNumber(uniquePatientNumber());
        patient.setFullName(request.fullName().trim());
        patient.setGender(request.gender() == null ? Gender.UNKNOWN : request.gender());
        patient.setAge(request.age());
        patient.setPhone(blankToNull(request.phone()));
        patient.setAddress(blankToNull(request.address()));
        patient.setInsuranceProvider(blankToNull(request.insuranceProvider()));
        patient.setBloodGroup(BloodGroup.UNKNOWN);
        patient.setPriority(PatientPriority.STABLE);
        patient.setStatus(PatientStatus.REGISTERED);
        patient.setLastVisitDate(LocalDate.now());
        syncAllergies(patient, request.allergies());
        Patient saved = patients.save(patient);
        logWorkflow("PATIENT_REGISTERED", saved, actor, null, PatientStatus.REGISTERED);
        return mapper.toDetail(saved);
    }

    public PatientDetailResponse updatePatient(Long id, PatientRegistrationRequest request) {
        requireAnyRole(UserRole.RECEPTIONIST);
        Patient patient = patient(id);
        requirePatientAccess(currentUser(), patient);
        patient.setFullName(request.fullName().trim());
        patient.setGender(request.gender() == null ? Gender.UNKNOWN : request.gender());
        patient.setAge(request.age());
        patient.setPhone(blankToNull(request.phone()));
        patient.setAddress(blankToNull(request.address()));
        patient.setInsuranceProvider(blankToNull(request.insuranceProvider()));
        patient.getAllergies().clear();
        syncAllergies(patient, request.allergies());
        return mapper.toDetail(patient);
    }

    public void deletePatient(Long id) {
        requireRole(UserRole.ADMIN);
        Patient patient = patient(id);
        patients.delete(patient);
    }

    public AppointmentRequest bookAppointment(AppointmentRequest request) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.RECEPTIONIST);
        Appointment appointment = new Appointment();
        Patient patient = patient(request.patientId());
        appointment.setPatient(patient);
        StaffUser assignedDoctor = request.assignedDoctorId() == null ? null : staff(request.assignedDoctorId());
        if (assignedDoctor == null) {
            throw new BusinessRuleException("A doctor must be assigned before the patient can enter clinical workflow.");
        }
        if (assignedDoctor != null && assignedDoctor.getRole() != UserRole.DOCTOR && assignedDoctor.getRole() != UserRole.CLINICIAN) {
            throw new BusinessRuleException("Appointments can only be assigned to doctors.");
        }
        appointment.setAssignedDoctor(assignedDoctor);
        appointment.setScheduledAt(request.scheduledAt());
        appointment.setType(request.type() == null ? AppointmentType.CONSULTATION : request.type());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReason(blankToNull(request.reason()));
        appointments.save(appointment);
        changePatientStatus(patient, PatientStatus.WAITING_FOR_NURSE_ASSESSMENT, actor, "APPOINTMENT_BOOKED");
        return request;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listAppointments() {
        StaffUser actor = currentUser();
        return appointments.findAll().stream()
                .filter(appointment -> canViewAppointment(actor, appointment))
                .map(mapper::toAppointment)
                .toList();
    }

    public void deleteAppointment(Long id) {
        requireAnyRole(UserRole.RECEPTIONIST);
        if (!appointments.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found.");
        }
        appointments.deleteById(id);
    }

    public TriageRecordRequest saveTriage(TriageRecordRequest request) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.NURSE);
        Patient patient = patient(request.patientId());
        requirePatientAccess(actor, patient);
        requirePatientStatus(patient, PatientStatus.WAITING_FOR_NURSE_ASSESSMENT);
        StaffUser nurse = actor;
        if (nurse.getRole() != UserRole.NURSE) {
            throw new BusinessRuleException("Triage records can only be assigned to nurses.");
        }
        Encounter encounter = request.encounterId() == null ? openEncounter(patient, nurse.getId()) : encounter(request.encounterId());
        encounter.setStatus(EncounterStatus.IN_TRIAGE);

        TriageRecord triage = new TriageRecord();
        triage.setRecordedAt(LocalDateTime.now());
        triage.setChiefComplaint(request.chiefComplaint());
        triage.setCategory(request.category() == null ? TriageCategory.NOT_ASSIGNED : request.category());
        triage.setPriority(request.priority() == null ? PatientPriority.STABLE : request.priority());
        triage.setEncounter(encounter);
        triage.setPatient(patient);
        triage.setNurse(nurse);
        if (request.vitalSign() != null) {
            VitalSign vital = new VitalSign();
            vital.setBloodPressure(request.vitalSign().bloodPressure());
            vital.setTemperatureCelsius(request.vitalSign().temperatureCelsius());
            vital.setPulseBpm(request.vitalSign().pulseBpm());
            vital.setOxygenSaturation(request.vitalSign().oxygenSaturation());
            vital.setWeightKg(request.vitalSign().weightKg());
            vital.setHeightCm(request.vitalSign().heightCm());
            vital.setTriageRecord(triage);
            triage.setVitalSign(vital);
        }
        patient.setPriority(triage.getPriority());
        patient.setLastVisitDate(LocalDate.now());
        triageRecords.save(triage);
        changePatientStatus(patient, PatientStatus.READY_FOR_DOCTOR_CONSULTATION, actor, "NURSE_ASSESSMENT_COMPLETED");
        return request;
    }

    public ConsultationRequest saveConsultation(ConsultationRequest request) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.DOCTOR, UserRole.CLINICIAN);
        Patient patient = patient(request.patientId());
        requirePatientAccess(actor, patient);
        requirePatientStatus(patient, PatientStatus.READY_FOR_DOCTOR_CONSULTATION, PatientStatus.LABORATORY_RESULTS_AVAILABLE);
        StaffUser doctor = actor;
        if (doctor.getRole() != UserRole.DOCTOR && doctor.getRole() != UserRole.CLINICIAN) {
            throw new BusinessRuleException("Consultations can only be assigned to doctors.");
        }
        Encounter encounter = request.encounterId() == null ? openEncounter(patient, doctor.getId()) : encounter(request.encounterId());
        encounter.setStatus(EncounterStatus.IN_CONSULTATION);

        Consultation consultation = new Consultation();
        consultation.setConsultedAt(LocalDateTime.now());
        consultation.setClinicalNotes(blankToNull(request.clinicalNotes()));
        consultation.setTreatmentPlan(blankToNull(request.treatmentPlan()));
        consultation.setEncounter(encounter);
        consultation.setPatient(patient);
        consultation.setDoctor(doctor);
        if (request.diagnosisCodes() != null) {
            request.diagnosisCodes().stream().filter(code -> !code.isBlank())
                    .map(this::diagnosis)
                    .forEach(consultation.getDiagnoses()::add);
        }
        consultations.save(consultation);
        if (request.requestedLabTests() != null && !request.requestedLabTests().isEmpty()) {
            createLabOrder(new LabOrderRequest(patient.getId(), consultation.getId(), doctor.getId(), "From consultation", request.requestedLabTests()));
        }
        if (request.prescriptionItems() != null && !request.prescriptionItems().isEmpty()) {
            createPrescription(patient, consultation, doctor.getId(), request.prescriptionItems());
        }
        patient.setLastVisitDate(LocalDate.now());
        if (request.requestedLabTests() != null && !request.requestedLabTests().isEmpty()) {
            changePatientStatus(patient, PatientStatus.WAITING_FOR_LABORATORY, actor, "LABORATORY_REQUESTED");
        } else if (request.prescriptionItems() != null && !request.prescriptionItems().isEmpty()) {
            changePatientStatus(patient, PatientStatus.WAITING_FOR_PHARMACY, actor, "PRESCRIPTION_CREATED");
        } else {
            changePatientStatus(patient, PatientStatus.READY_FOR_BILLING, actor, "CONSULTATION_COMPLETED");
        }
        return request;
    }

    public LabOrderResponse createLabOrder(LabOrderRequest request) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.DOCTOR, UserRole.CLINICIAN);
        Patient patient = patient(request.patientId());
        requirePatientAccess(actor, patient);
        requirePatientStatus(patient, PatientStatus.READY_FOR_DOCTOR_CONSULTATION, PatientStatus.LABORATORY_RESULTS_AVAILABLE);
        StaffUser requestedBy = actor;
        if (requestedBy.getRole() != UserRole.DOCTOR && requestedBy.getRole() != UserRole.CLINICIAN) {
            throw new BusinessRuleException("Lab orders can only be requested by doctors.");
        }
        LabOrder order = new LabOrder();
        order.setOrderNumber(uniqueLabOrderNumber());
        order.setRequestedAt(LocalDateTime.now());
        order.setStatus(LabOrderStatus.REQUESTED);
        order.setNotes(blankToNull(request.notes()));
        order.setPatient(patient);
        order.setConsultation(request.consultationId() == null ? null : consultation(request.consultationId()));
        order.setRequestedBy(requestedBy);
        for (String name : request.tests()) {
            LabOrderItem item = new LabOrderItem();
            item.setLabOrder(order);
            item.setLabTest(labTest(name));
            item.setStatus(LabOrderStatus.REQUESTED);
            item.setPriority("Routine");
            order.getItems().add(item);
        }
        LabOrder saved = labOrders.save(order);
        changePatientStatus(patient, PatientStatus.WAITING_FOR_LABORATORY, actor, "LABORATORY_REQUESTED");
        return mapper.toLabOrder(saved);
    }

    @Transactional(readOnly = true)
    public List<LabOrderResponse> listLabOrders() {
        StaffUser actor = currentUser();
        return labOrders.findAll().stream()
                .filter(order -> canViewLabOrder(actor, order))
                .map(mapper::toLabOrder)
                .toList();
    }

    public LabResultResponse publishDemoResult(Long orderItemId) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.LAB_TECHNICIAN);
        LabOrderItem item = labOrderItems.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab order item not found."));
        Patient patient = item.getLabOrder().getPatient();
        requirePatientStatus(patient, PatientStatus.WAITING_FOR_LABORATORY);
        LabResult result = new LabResult();
        result.setLabOrderItem(item);
        result.setResultValues("WBC 7.2; RBC 4.9; Platelets 210");
        result.setInterpretation("Within expected reference ranges.");
        result.setPublishedAt(LocalDateTime.now());
        item.setStatus(LabOrderStatus.RESULT_READY);
        item.getLabOrder().setStatus(LabOrderStatus.RESULT_READY);
        item.setLabResult(result);
        LabResult saved = labResults.save(result);
        if (item.getLabOrder().getItems().stream().allMatch(orderItem -> orderItem.getStatus() == LabOrderStatus.RESULT_READY)) {
            changePatientStatus(patient, PatientStatus.LABORATORY_RESULTS_AVAILABLE, actor, "LABORATORY_RESULTS_AVAILABLE");
        }
        return mapper.toLabResult(saved);
    }

    public InvoiceResponse createInvoice(InvoiceRequest request) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.BILLING);
        Patient patient = patient(request.patientId());
        requirePatientStatus(patient, PatientStatus.READY_FOR_BILLING);
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(uniqueInvoiceNumber());
        invoice.setIssuedAt(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.GENERATED);
        invoice.setPatient(patient);
        invoice.setEncounter(request.encounterId() == null ? null : encounter(request.encounterId()));
        invoice.setTotalAmount(BigDecimal.ZERO);
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceLineItemDto dto : request.items()) {
            BigDecimal lineTotal = dto.lineTotal() != null ? dto.lineTotal() : dto.unitAmount().multiply(BigDecimal.valueOf(dto.quantity()));
            InvoiceLineItem item = new InvoiceLineItem();
            item.setInvoice(invoice);
            item.setLabel(dto.label());
            item.setQuantity(dto.quantity());
            item.setUnitAmount(dto.unitAmount());
            item.setLineTotal(lineTotal);
            invoice.getLineItems().add(item);
            total = total.add(lineTotal);
        }
        invoice.setTotalAmount(total);
        Invoice saved = invoices.save(invoice);
        logWorkflow("INVOICE_GENERATED", patient, actor, patient.getStatus(), patient.getStatus());
        return mapper.toInvoice(saved);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> listInvoices() {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.ADMIN, UserRole.BILLING);
        return invoices.findAll().stream()
                .filter(invoice -> actor.getRole() == UserRole.ADMIN || invoice.getPatient().getStatus() == PatientStatus.READY_FOR_BILLING)
                .map(mapper::toInvoice)
                .toList();
    }

    public InvoiceResponse recordPayment(PaymentRequest request) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.BILLING);
        Invoice invoice = invoices.findById(request.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found."));
        requirePatientStatus(invoice.getPatient(), PatientStatus.READY_FOR_BILLING);
        Payment payment = new Payment();
        payment.setReferenceNumber(uniquePaymentReference());
        payment.setAmount(request.amount());
        payment.setMethod(request.method());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        payment.setInvoice(invoice);
        invoice.getPayments().add(payment);
        payments.save(payment);
        BigDecimal paid = invoice.getPayments().stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setStatus(paid.compareTo(invoice.getTotalAmount()) >= 0 ? InvoiceStatus.PAID : InvoiceStatus.PARTIALLY_PAID);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            changePatientStatus(invoice.getPatient(), PatientStatus.COMPLETED, actor, "PAYMENT_COMPLETED");
        }
        return mapper.toInvoice(invoice);
    }

    @Transactional(readOnly = true)
    public List<MedicationResponse> listMedications() {
        requireAnyRole(UserRole.ADMIN, UserRole.PHARMACIST);
        return medications.findAll().stream().map(mapper::toMedication).toList();
    }

    public MedicationResponse createMedication(MedicationRequest request) {
        requireAnyRole(UserRole.PHARMACIST);
        medications.findByNameIgnoreCase(request.name()).ifPresent(existing -> {
            throw new BusinessRuleException("Medication already exists.");
        });
        Medication medication = new Medication();
        applyMedicationRequest(medication, request);
        return mapper.toMedication(medications.save(medication));
    }

    public MedicationResponse updateMedication(Long id, MedicationRequest request) {
        requireAnyRole(UserRole.PHARMACIST);
        Medication medication = medications.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found."));
        applyMedicationRequest(medication, request);
        return mapper.toMedication(medication);
    }

    public void deleteMedication(Long id) {
        requireAnyRole(UserRole.PHARMACIST);
        if (!medications.existsById(id)) {
            throw new ResourceNotFoundException("Medication not found.");
        }
        medications.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionQueueResponse> prescriptionQueue() {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.ADMIN, UserRole.PHARMACIST);
        return prescriptionItems.findAll().stream()
                .filter(item -> item.getDispense() == null)
                .filter(item -> item.getPrescription().getStatus() == PrescriptionStatus.ACTIVE)
                .filter(item -> actor.getRole() == UserRole.ADMIN || item.getPrescription().getPatient().getStatus() == PatientStatus.WAITING_FOR_PHARMACY)
                .map(mapper::toPrescriptionQueue)
                .toList();
    }

    public PharmacyDispenseRequest dispense(PharmacyDispenseRequest request) {
        StaffUser actor = currentUser();
        requireAnyRole(actor, UserRole.PHARMACIST);
        PrescriptionItem item = prescriptionItems.findById(request.prescriptionItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Prescription item not found."));
        Patient patient = item.getPrescription().getPatient();
        requirePatientStatus(patient, PatientStatus.WAITING_FOR_PHARMACY);
        if (item.getDispense() != null) {
            throw new BusinessRuleException("This prescription item has already been dispensed.");
        }
        Medication medication = item.getMedication();
        int quantity = request.quantityDispensed() == null ? item.getQuantity() : request.quantityDispensed();
        if (medication.getStockQuantity() != null && medication.getStockQuantity() < quantity) {
            throw new BusinessRuleException("Insufficient medication stock.");
        }
        if (medication.getStockQuantity() != null) {
            medication.setStockQuantity(medication.getStockQuantity() - quantity);
            medication.setInventoryStatus(medication.getStockQuantity() <= 0 ? InventoryStatus.OUT_OF_STOCK :
                    medication.getStockQuantity() <= medication.getReorderLevel() ? InventoryStatus.LOW_STOCK : InventoryStatus.IN_STOCK);
        }
        PharmacyDispense dispense = new PharmacyDispense();
        dispense.setDispenseNumber(NumberGenerator.dispenseNumber());
        dispense.setPrescriptionItem(item);
        StaffUser dispensedBy = actor;
        if (dispensedBy.getRole() != UserRole.PHARMACIST) {
            throw new BusinessRuleException("Dispenses can only be assigned to pharmacists.");
        }
        dispense.setDispensedBy(dispensedBy);
        dispense.setQuantityDispensed(quantity);
        dispense.setDispensedAt(LocalDateTime.now());
        dispense.setStatus(DispenseStatus.DISPENSED);
        item.setDispense(dispense);
        dispenses.save(dispense);
        if (item.getPrescription().getItems().stream().allMatch(prescriptionItem -> prescriptionItem.getDispense() != null)) {
            item.getPrescription().setStatus(PrescriptionStatus.DISPENSED);
            changePatientStatus(patient, PatientStatus.READY_FOR_BILLING, actor, "PRESCRIPTION_DISPENSED");
        } else {
            item.getPrescription().setStatus(PrescriptionStatus.PARTIALLY_DISPENSED);
            logWorkflow("PRESCRIPTION_ITEM_DISPENSED", patient, actor, patient.getStatus(), patient.getStatus());
        }
        return request;
    }

    public StaffUserResponse createStaff(StaffUserCreateRequest request) {
        requireRole(UserRole.ADMIN);
        staffUsers.findByEmailIgnoreCase(request.email()).ifPresent(existing -> {
            throw new BusinessRuleException("Email is already registered.");
        });
        StaffUser user = new StaffUser();
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.temporaryPassword()));
        user.setRole(request.role());
        user.setStatus(AccountStatus.ACTIVE);
        user.setDepartment(request.departmentId() == null ? null : department(request.departmentId()));
        user.setPhone(blankToNull(request.phone()));
        return mapper.toStaff(staffUsers.save(user));
    }

    public StaffUserResponse updateStaff(Long id, StaffUserUpdateRequest request) {
        requireRole(UserRole.ADMIN);
        StaffUser user = staff(id);
        staffUsers.findByEmailIgnoreCase(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Email is already registered.");
                });
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());
        user.setRole(request.role());
        user.setStatus(request.status());
        user.setDepartment(request.departmentId() == null ? null : department(request.departmentId()));
        user.setPhone(blankToNull(request.phone()));
        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        }
        return mapper.toStaff(user);
    }

    @Transactional(readOnly = true)
    public List<StaffUserResponse> listStaff() {
        requireRole(UserRole.ADMIN);
        return staffUsers.findAll().stream().map(mapper::toStaff).toList();
    }

    public void deleteStaff(Long id) {
        requireRole(UserRole.ADMIN);
        if (!staffUsers.existsById(id)) {
            throw new ResourceNotFoundException("Staff user not found.");
        }
        staffUsers.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse dashboardStats() {
        StaffUser actor = currentUser();
        BigDecimal revenue = canViewFinancials(actor) ? invoices.findAll().stream()
                .filter(invoice -> invoice.getIssuedAt().getMonth() == LocalDateTime.now().getMonth())
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
        long lowStock = actor.getRole() == UserRole.PHARMACIST ? prescriptionItems.findAll().stream()
                .filter(item -> item.getDispense() == null)
                .filter(item -> item.getPrescription().getStatus() == PrescriptionStatus.ACTIVE)
                .filter(item -> item.getPrescription().getPatient().getStatus() == PatientStatus.WAITING_FOR_PHARMACY)
                .count() : canViewPharmacy(actor) ? medications.findAll().stream()
                .filter(m -> m.getInventoryStatus() == InventoryStatus.LOW_STOCK || m.getInventoryStatus() == InventoryStatus.OUT_OF_STOCK)
                .count() : 0;
        long availableBeds = canViewBeds(actor) ? beds.findAll().stream().filter(b -> b.getStatus() == BedStatus.AVAILABLE).count() : 0;
        long pendingLabs = canViewLabs(actor) ? labOrders.findAll().stream()
                .filter(order -> canViewLabOrder(actor, order))
                .filter(order -> actor.getRole() == UserRole.DOCTOR || actor.getRole() == UserRole.CLINICIAN
                        ? order.getStatus() == LabOrderStatus.RESULT_READY
                        : order.getStatus() == LabOrderStatus.REQUESTED || order.getStatus() == LabOrderStatus.IN_PROGRESS)
                .count() : 0;
        long appointmentsToday = appointments.findAll().stream()
                .filter(appointment -> canViewAppointment(actor, appointment))
                .filter(a -> a.getScheduledAt().toLocalDate().equals(LocalDate.now()))
                .count();
        long pendingInvites = actor.getRole() == UserRole.ADMIN ? staffUsers.findAll().stream().filter(u -> u.getStatus() == AccountStatus.PENDING_INVITE).count() : 0;
        long visiblePatients = patients.findAll().stream().filter(patient -> canViewPatient(actor, patient)).count();
        return new DashboardStatsResponse(visiblePatients, appointmentsToday, availableBeds, revenue, pendingLabs, lowStock, pendingInvites);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> notifications() {
        StaffUser actor = currentUser();
        return notifications.findAll().stream()
                .filter(notification -> actor.getRole() == UserRole.ADMIN || notification.getRecipient() == null || sameStaff(notification.getRecipient(), actor))
                .map(mapper::toNotification)
                .toList();
    }

    public void deleteNotification(Long id) {
        requireRole(UserRole.ADMIN);
        if (!notifications.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found.");
        }
        notifications.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<WardResponse> listWards() {
        requireAnyRole(UserRole.ADMIN, UserRole.NURSE);
        return wards.findAll().stream().map(mapper::toWard).toList();
    }

    @Transactional(readOnly = true)
    public List<BedResponse> listBeds() {
        requireAnyRole(UserRole.ADMIN, UserRole.NURSE);
        return beds.findAll().stream().map(mapper::toBed).toList();
    }

    @Transactional(readOnly = true)
    public ReportSummaryResponse reportSummary() {
        requireRole(UserRole.ADMIN);
        BigDecimal revenue = invoices.findAll().stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long availableBeds = beds.findAll().stream().filter(bed -> bed.getStatus() == BedStatus.AVAILABLE).count();
        long occupiedBeds = beds.findAll().stream().filter(bed -> bed.getStatus() == BedStatus.OCCUPIED).count();
        long lowStock = medications.findAll().stream()
                .filter(m -> m.getInventoryStatus() == InventoryStatus.LOW_STOCK || m.getInventoryStatus() == InventoryStatus.OUT_OF_STOCK)
                .count();
        return new ReportSummaryResponse(
                patients.count(),
                appointments.count(),
                labOrders.count(),
                invoices.count(),
                availableBeds,
                occupiedBeds,
                lowStock,
                revenue
        );
    }

    @Transactional(readOnly = true)
    public HospitalSettingsResponse hospitalSettings() {
        requireRole(UserRole.ADMIN);
        return new HospitalSettingsResponse(
                "MediFlow Hospital",
                "Role-based access is enforced for clinical, pharmacy, billing, reporting, and admin workflows.",
                Arrays.stream(UserRole.values()).map(Enum::name).toList()
        );
    }

    private StaffUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new AccessDeniedException("Authentication required.");
        }
        return staffUsers.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user is no longer available."));
    }

    private void requireRole(UserRole role) {
        requireAnyRole(currentUser(), role);
    }

    private void requireAnyRole(UserRole... roles) {
        requireAnyRole(currentUser(), roles);
    }

    private void requireAnyRole(StaffUser actor, UserRole... roles) {
        for (UserRole role : roles) {
            if (actor.getRole() == role) {
                return;
            }
        }
        throw new AccessDeniedException("Access Denied");
    }

    private void requirePatientAccess(StaffUser actor, Patient patient) {
        if (!canViewPatient(actor, patient)) {
            throw new AccessDeniedException("Access Denied");
        }
    }

    private boolean canViewPatient(StaffUser actor, Patient patient) {
        return switch (actor.getRole()) {
            case ADMIN -> true;
            case RECEPTIONIST -> hasStatus(patient, PatientStatus.REGISTERED, PatientStatus.WAITING_FOR_NURSE_ASSESSMENT);
            case BILLING -> hasStatus(patient, PatientStatus.READY_FOR_BILLING, PatientStatus.COMPLETED);
            case NURSE -> hasStatus(patient, PatientStatus.WAITING_FOR_NURSE_ASSESSMENT);
            case DOCTOR, CLINICIAN -> isDoctorAssignedToPatient(actor, patient)
                    && hasStatus(patient, PatientStatus.READY_FOR_DOCTOR_CONSULTATION, PatientStatus.LABORATORY_RESULTS_AVAILABLE);
            case LAB_TECHNICIAN, PHARMACIST -> false;
        };
    }

    private boolean isDoctorAssignedToPatient(StaffUser actor, Patient patient) {
        return appointments.findAll().stream().anyMatch(appointment -> samePatient(appointment.getPatient(), patient) && sameStaff(appointment.getAssignedDoctor(), actor))
                || encounters.findAll().stream().anyMatch(encounter -> samePatient(encounter.getPatient(), patient) && sameStaff(encounter.getPrimaryClinician(), actor))
                || consultations.findAll().stream().anyMatch(consultation -> samePatient(consultation.getPatient(), patient) && sameStaff(consultation.getDoctor(), actor))
                || prescriptions.findAll().stream().anyMatch(prescription -> samePatient(prescription.getPatient(), patient) && sameStaff(prescription.getPrescribedBy(), actor))
                || labOrders.findAll().stream().anyMatch(order -> samePatient(order.getPatient(), patient) && sameStaff(order.getRequestedBy(), actor));
    }

    private boolean canViewAppointment(StaffUser actor, Appointment appointment) {
        return switch (actor.getRole()) {
            case ADMIN -> true;
            case RECEPTIONIST -> true;
            case NURSE -> hasStatus(appointment.getPatient(), PatientStatus.WAITING_FOR_NURSE_ASSESSMENT);
            case DOCTOR, CLINICIAN -> sameStaff(appointment.getAssignedDoctor(), actor)
                    && hasStatus(appointment.getPatient(), PatientStatus.READY_FOR_DOCTOR_CONSULTATION, PatientStatus.LABORATORY_RESULTS_AVAILABLE);
            case BILLING -> hasStatus(appointment.getPatient(), PatientStatus.READY_FOR_BILLING, PatientStatus.COMPLETED);
            case LAB_TECHNICIAN, PHARMACIST -> false;
        };
    }

    private boolean canViewLabOrder(StaffUser actor, LabOrder order) {
        return switch (actor.getRole()) {
            case ADMIN -> true;
            case LAB_TECHNICIAN -> order.getStatus() == LabOrderStatus.REQUESTED || order.getStatus() == LabOrderStatus.IN_PROGRESS;
            case DOCTOR, CLINICIAN -> sameStaff(order.getRequestedBy(), actor)
                    && hasStatus(order.getPatient(), PatientStatus.LABORATORY_RESULTS_AVAILABLE);
            default -> false;
        };
    }

    private boolean canViewLabs(StaffUser actor) {
        return actor.getRole() == UserRole.ADMIN
                || actor.getRole() == UserRole.LAB_TECHNICIAN
                || actor.getRole() == UserRole.DOCTOR
                || actor.getRole() == UserRole.CLINICIAN;
    }

    private boolean canViewPharmacy(StaffUser actor) {
        return actor.getRole() == UserRole.ADMIN || actor.getRole() == UserRole.PHARMACIST;
    }

    private boolean canViewFinancials(StaffUser actor) {
        return actor.getRole() == UserRole.ADMIN || actor.getRole() == UserRole.BILLING;
    }

    private boolean canViewBeds(StaffUser actor) {
        return actor.getRole() == UserRole.ADMIN || actor.getRole() == UserRole.NURSE;
    }

    private void requirePatientStatus(Patient patient, PatientStatus... statuses) {
        if (!hasStatus(patient, statuses)) {
            throw new BusinessRuleException("Patient is currently " + patient.getStatus() + " and is not assigned to this workflow stage.");
        }
    }

    private boolean hasStatus(Patient patient, PatientStatus... statuses) {
        PatientStatus actual = normalizeStatus(patient.getStatus());
        for (PatientStatus status : statuses) {
            if (actual == status) {
                return true;
            }
        }
        return false;
    }

    private PatientStatus normalizeStatus(PatientStatus status) {
        return switch (status) {
            case ACTIVE, OUTPATIENT -> PatientStatus.REGISTERED;
            case ADMITTED -> PatientStatus.READY_FOR_DOCTOR_CONSULTATION;
            case DISCHARGED, INACTIVE -> PatientStatus.COMPLETED;
            default -> status;
        };
    }

    private void changePatientStatus(Patient patient, PatientStatus nextStatus, StaffUser actor, String action) {
        PatientStatus previous = patient.getStatus();
        patient.setStatus(nextStatus);
        logWorkflow(action, patient, actor, previous, nextStatus);
    }

    private void logWorkflow(String action, Patient patient, StaffUser actor, PatientStatus previous, PatientStatus next) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setActor(actor);
        log.setPatient(patient);
        log.setDetails("patient=" + patient.getPatientNumber()
                + "; previousStatus=" + (previous == null ? "NONE" : previous)
                + "; nextStatus=" + next);
        activityLogs.save(log);
    }

    private boolean samePatient(Patient left, Patient right) {
        return left != null && right != null && Objects.equals(left.getId(), right.getId());
    }

    private boolean sameStaff(StaffUser left, StaffUser right) {
        return left != null && right != null && Objects.equals(left.getId(), right.getId());
    }

    private Patient patient(Long id) {
        return patients.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found."));
    }

    private StaffUser staff(Long id) {
        return staffUsers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Staff user not found."));
    }

    private Department department(Long id) {
        return departments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found."));
    }

    private Encounter encounter(Long id) {
        return encounters.findById(id).orElseThrow(() -> new ResourceNotFoundException("Encounter not found."));
    }

    private Consultation consultation(Long id) {
        return consultations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Consultation not found."));
    }

    private Encounter openEncounter(Patient patient, Long clinicianId) {
        Encounter encounter = new Encounter();
        encounter.setEncounterNumber(NumberGenerator.encounterNumber());
        encounter.setStartedAt(LocalDateTime.now());
        encounter.setStatus(EncounterStatus.OPEN);
        encounter.setPatient(patient);
        encounter.setPrimaryClinician(clinicianId == null ? null : staff(clinicianId));
        return encounters.save(encounter);
    }

    private DiagnosisCode diagnosis(String code) {
        String normalized = code.trim().toUpperCase();
        return diagnosisCodes.findByCode(normalized)
                .orElseGet(() -> {
                    DiagnosisCode diagnosisCode = new DiagnosisCode();
                    diagnosisCode.setCode(normalized);
                    diagnosisCode.setDescription("User-entered diagnosis");
                    return diagnosisCodes.save(diagnosisCode);
                });
    }

    private LabTest labTest(String name) {
        String normalized = name.trim();
        return labTests.findByNameIgnoreCase(normalized)
                .orElseGet(() -> {
                    LabTest labTest = new LabTest();
                    labTest.setName(normalized);
                    labTest.setCode(normalized.toUpperCase().replaceAll("[^A-Z0-9]+", "_"));
                    labTest.setCategory("General");
                    labTest.setPrice(BigDecimal.valueOf(40));
                    return labTests.save(labTest);
                });
    }

    private void createPrescription(Patient patient, Consultation consultation, Long doctorId, List<PrescriptionItemDto> itemDtos) {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionNumber(NumberGenerator.prescriptionNumber());
        prescription.setPrescribedAt(LocalDateTime.now());
        prescription.setStatus(PrescriptionStatus.ACTIVE);
        prescription.setPatient(patient);
        prescription.setConsultation(consultation);
        prescription.setPrescribedBy(doctorId == null ? null : staff(doctorId));
        for (PrescriptionItemDto dto : itemDtos) {
            Medication medication = dto.medicationId() == null ? medication(dto.medicationName()) :
                    medications.findById(dto.medicationId()).orElseThrow(() -> new ResourceNotFoundException("Medication not found."));
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(prescription);
            item.setMedication(medication);
            item.setDosage(dto.dosage());
            item.setFrequency(dto.frequency());
            item.setDuration(dto.duration());
            item.setQuantity(dto.quantity());
            prescription.getItems().add(item);
        }
        prescriptions.save(prescription);
    }

    private Medication medication(String name) {
        return medications.findByNameIgnoreCase(name).orElseGet(() -> {
            Medication medication = new Medication();
            medication.setName(name);
            medication.setStrength("");
            medication.setDosageForm("Tablet");
            medication.setStockQuantity(100);
            medication.setReorderLevel(20);
            medication.setUnitPrice(BigDecimal.valueOf(5));
            medication.setInventoryStatus(InventoryStatus.IN_STOCK);
            return medications.save(medication);
        });
    }

    private void applyMedicationRequest(Medication medication, MedicationRequest request) {
        medication.setName(request.name().trim());
        medication.setStrength(blankToNull(request.strength()));
        medication.setDosageForm(blankToNull(request.dosageForm()));
        medication.setStockQuantity(request.stockQuantity() == null ? 0 : request.stockQuantity());
        medication.setReorderLevel(request.reorderLevel() == null ? 0 : request.reorderLevel());
        medication.setUnitPrice(request.unitPrice() == null ? BigDecimal.ZERO : request.unitPrice());
        if (request.inventoryStatus() != null) {
            medication.setInventoryStatus(request.inventoryStatus());
        } else if (medication.getStockQuantity() <= 0) {
            medication.setInventoryStatus(InventoryStatus.OUT_OF_STOCK);
        } else if (medication.getStockQuantity() <= medication.getReorderLevel()) {
            medication.setInventoryStatus(InventoryStatus.LOW_STOCK);
        } else {
            medication.setInventoryStatus(InventoryStatus.IN_STOCK);
        }
    }

    private void syncAllergies(Patient patient, String allergyText) {
        if (allergyText == null || allergyText.isBlank()) {
            return;
        }
        Arrays.stream(allergyText.split(","))
                .map(String::trim)
                .filter(name -> !name.isBlank() && !"none".equalsIgnoreCase(name) && !"none recorded".equalsIgnoreCase(name))
                .map(name -> allergies.findByNameIgnoreCase(name).orElseGet(() -> {
                    Allergy allergy = new Allergy();
                    allergy.setName(name);
                    return allergies.save(allergy);
                }))
                .forEach(patient.getAllergies()::add);
    }

    private String uniquePatientNumber() {
        String value;
        do {
            value = NumberGenerator.patientNumber();
        } while (patients.findByPatientNumber(value).isPresent());
        return value;
    }

    private String uniqueLabOrderNumber() {
        String value;
        do {
            value = NumberGenerator.labOrderNumber();
        } while (labOrders.findByOrderNumber(value).isPresent());
        return value;
    }

    private String uniqueInvoiceNumber() {
        String value;
        do {
            value = NumberGenerator.invoiceNumber();
        } while (invoices.findByInvoiceNumber(value).isPresent());
        return value;
    }

    private String uniquePaymentReference() {
        String value;
        do {
            value = NumberGenerator.paymentReference();
        } while (payments.findByReferenceNumber(value).isPresent());
        return value;
    }

    private String dashboardUrl(UserRole role) {
        return switch (role) {
            case ADMIN -> "dashboard.html?role=admin";
            case RECEPTIONIST -> "dashboard.html?role=receptionist";
            case DOCTOR, CLINICIAN -> "dashboard.html?role=doctor";
            case NURSE -> "dashboard.html?role=nurse";
            case LAB_TECHNICIAN -> "dashboard.html?role=laboratory";
            case PHARMACIST -> "dashboard.html?role=pharmacy";
            case BILLING -> "dashboard.html?role=billing";
        };
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
