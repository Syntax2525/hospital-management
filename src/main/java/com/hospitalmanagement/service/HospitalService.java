package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.*;
import com.hospitalmanagement.enums.*;
import com.hospitalmanagement.exception.BusinessRuleException;
import com.hospitalmanagement.exception.ResourceNotFoundException;
import com.hospitalmanagement.mapper.HospitalMapper;
import com.hospitalmanagement.model.*;
import com.hospitalmanagement.repository.*;
import com.hospitalmanagement.security.TokenService;
import com.hospitalmanagement.util.NumberGenerator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class HospitalService {
    private final PatientRepository patients;
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

    public HospitalService(
            PatientRepository patients,
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

    @Transactional(readOnly = true)
    public AuthSessionResponse login(AuthLoginRequest request) {
        StaffUser user = staffUsers.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (user.getStatus() != AccountStatus.ACTIVE || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new AuthSessionResponse(true, user.getFullName(), user.getRole(), user.getEmail(), tokenService.issue(user));
    }

    @Transactional(readOnly = true)
    public List<PatientSummaryResponse> listPatients() {
        return patients.findAll().stream().map(mapper::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<PatientDetailResponse> listPatientDetails() {
        return patients.findAll().stream().map(mapper::toDetail).toList();
    }

    @Transactional(readOnly = true)
    public PatientDetailResponse getPatient(Long id) {
        return mapper.toDetail(patient(id));
    }

    public PatientDetailResponse registerPatient(PatientRegistrationRequest request) {
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
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setLastVisitDate(LocalDate.now());
        syncAllergies(patient, request.allergies());
        return mapper.toDetail(patients.save(patient));
    }

    public PatientDetailResponse updatePatient(Long id, PatientRegistrationRequest request) {
        Patient patient = patient(id);
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
        Patient patient = patient(id);
        patients.delete(patient);
    }

    public AppointmentRequest bookAppointment(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient(request.patientId()));
        appointment.setAssignedDoctor(request.assignedDoctorId() == null ? null : staff(request.assignedDoctorId()));
        appointment.setScheduledAt(request.scheduledAt());
        appointment.setType(request.type() == null ? AppointmentType.CONSULTATION : request.type());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReason(blankToNull(request.reason()));
        appointments.save(appointment);
        return request;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listAppointments() {
        return appointments.findAll().stream().map(mapper::toAppointment).toList();
    }

    public void deleteAppointment(Long id) {
        if (!appointments.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found.");
        }
        appointments.deleteById(id);
    }

    public TriageRecordRequest saveTriage(TriageRecordRequest request) {
        Patient patient = patient(request.patientId());
        Encounter encounter = request.encounterId() == null ? openEncounter(patient, request.nurseId()) : encounter(request.encounterId());
        encounter.setStatus(EncounterStatus.IN_TRIAGE);

        TriageRecord triage = new TriageRecord();
        triage.setRecordedAt(LocalDateTime.now());
        triage.setChiefComplaint(request.chiefComplaint());
        triage.setCategory(request.category() == null ? TriageCategory.NOT_ASSIGNED : request.category());
        triage.setPriority(request.priority() == null ? PatientPriority.STABLE : request.priority());
        triage.setEncounter(encounter);
        triage.setPatient(patient);
        triage.setNurse(request.nurseId() == null ? null : staff(request.nurseId()));
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
        return request;
    }

    public ConsultationRequest saveConsultation(ConsultationRequest request) {
        Patient patient = patient(request.patientId());
        Encounter encounter = request.encounterId() == null ? openEncounter(patient, request.doctorId()) : encounter(request.encounterId());
        encounter.setStatus(EncounterStatus.IN_CONSULTATION);

        Consultation consultation = new Consultation();
        consultation.setConsultedAt(LocalDateTime.now());
        consultation.setClinicalNotes(blankToNull(request.clinicalNotes()));
        consultation.setTreatmentPlan(blankToNull(request.treatmentPlan()));
        consultation.setEncounter(encounter);
        consultation.setPatient(patient);
        consultation.setDoctor(request.doctorId() == null ? null : staff(request.doctorId()));
        if (request.diagnosisCodes() != null) {
            request.diagnosisCodes().stream().filter(code -> !code.isBlank())
                    .map(this::diagnosis)
                    .forEach(consultation.getDiagnoses()::add);
        }
        consultations.save(consultation);
        if (request.requestedLabTests() != null && !request.requestedLabTests().isEmpty()) {
            createLabOrder(new LabOrderRequest(patient.getId(), consultation.getId(), request.doctorId(), "From consultation", request.requestedLabTests()));
        }
        if (request.prescriptionItems() != null && !request.prescriptionItems().isEmpty()) {
            createPrescription(patient, consultation, request.doctorId(), request.prescriptionItems());
        }
        patient.setLastVisitDate(LocalDate.now());
        return request;
    }

    public LabOrderResponse createLabOrder(LabOrderRequest request) {
        Patient patient = patient(request.patientId());
        LabOrder order = new LabOrder();
        order.setOrderNumber(uniqueLabOrderNumber());
        order.setRequestedAt(LocalDateTime.now());
        order.setStatus(LabOrderStatus.REQUESTED);
        order.setNotes(blankToNull(request.notes()));
        order.setPatient(patient);
        order.setConsultation(request.consultationId() == null ? null : consultation(request.consultationId()));
        order.setRequestedBy(request.requestedById() == null ? null : staff(request.requestedById()));
        for (String name : request.tests()) {
            LabOrderItem item = new LabOrderItem();
            item.setLabOrder(order);
            item.setLabTest(labTest(name));
            item.setStatus(LabOrderStatus.REQUESTED);
            item.setPriority("Routine");
            order.getItems().add(item);
        }
        return mapper.toLabOrder(labOrders.save(order));
    }

    @Transactional(readOnly = true)
    public List<LabOrderResponse> listLabOrders() {
        return labOrders.findAll().stream().map(mapper::toLabOrder).toList();
    }

    public LabResultResponse publishDemoResult(Long orderItemId) {
        LabOrderItem item = labOrderItems.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab order item not found."));
        LabResult result = new LabResult();
        result.setLabOrderItem(item);
        result.setResultValues("WBC 7.2; RBC 4.9; Platelets 210");
        result.setInterpretation("Within expected reference ranges.");
        result.setPublishedAt(LocalDateTime.now());
        item.setStatus(LabOrderStatus.RESULT_READY);
        item.getLabOrder().setStatus(LabOrderStatus.RESULT_READY);
        item.setLabResult(result);
        return mapper.toLabResult(labResults.save(result));
    }

    public InvoiceResponse createInvoice(InvoiceRequest request) {
        Patient patient = patient(request.patientId());
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
        return mapper.toInvoice(invoices.save(invoice));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> listInvoices() {
        return invoices.findAll().stream().map(mapper::toInvoice).toList();
    }

    public InvoiceResponse recordPayment(PaymentRequest request) {
        Invoice invoice = invoices.findById(request.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found."));
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
        return mapper.toInvoice(invoice);
    }

    @Transactional(readOnly = true)
    public List<MedicationResponse> listMedications() {
        return medications.findAll().stream().map(mapper::toMedication).toList();
    }

    public MedicationResponse createMedication(MedicationRequest request) {
        medications.findByNameIgnoreCase(request.name()).ifPresent(existing -> {
            throw new BusinessRuleException("Medication already exists.");
        });
        Medication medication = new Medication();
        applyMedicationRequest(medication, request);
        return mapper.toMedication(medications.save(medication));
    }

    public MedicationResponse updateMedication(Long id, MedicationRequest request) {
        Medication medication = medications.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found."));
        applyMedicationRequest(medication, request);
        return mapper.toMedication(medication);
    }

    public void deleteMedication(Long id) {
        if (!medications.existsById(id)) {
            throw new ResourceNotFoundException("Medication not found.");
        }
        medications.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionQueueResponse> prescriptionQueue() {
        return prescriptionItems.findAll().stream()
                .filter(item -> item.getDispense() == null)
                .map(mapper::toPrescriptionQueue)
                .toList();
    }

    public PharmacyDispenseRequest dispense(PharmacyDispenseRequest request) {
        PrescriptionItem item = prescriptionItems.findById(request.prescriptionItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Prescription item not found."));
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
        dispense.setDispensedBy(request.dispensedById() == null ? null : staff(request.dispensedById()));
        dispense.setQuantityDispensed(quantity);
        dispense.setDispensedAt(LocalDateTime.now());
        dispense.setStatus(DispenseStatus.DISPENSED);
        item.setDispense(dispense);
        dispenses.save(dispense);
        return request;
    }

    public StaffUserResponse createStaff(StaffUserCreateRequest request) {
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

    @Transactional(readOnly = true)
    public List<StaffUserResponse> listStaff() {
        return staffUsers.findAll().stream().map(mapper::toStaff).toList();
    }

    public void deleteStaff(Long id) {
        if (!staffUsers.existsById(id)) {
            throw new ResourceNotFoundException("Staff user not found.");
        }
        staffUsers.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse dashboardStats() {
        BigDecimal revenue = invoices.findAll().stream()
                .filter(invoice -> invoice.getIssuedAt().getMonth() == LocalDateTime.now().getMonth())
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long lowStock = medications.findAll().stream()
                .filter(m -> m.getInventoryStatus() == InventoryStatus.LOW_STOCK || m.getInventoryStatus() == InventoryStatus.OUT_OF_STOCK)
                .count();
        long availableBeds = beds.findAll().stream().filter(b -> b.getStatus() == BedStatus.AVAILABLE).count();
        long pendingLabs = labOrders.findAll().stream().filter(o -> o.getStatus() == LabOrderStatus.REQUESTED || o.getStatus() == LabOrderStatus.IN_PROGRESS).count();
        long appointmentsToday = appointments.findAll().stream().filter(a -> a.getScheduledAt().toLocalDate().equals(LocalDate.now())).count();
        long pendingInvites = staffUsers.findAll().stream().filter(u -> u.getStatus() == AccountStatus.PENDING_INVITE).count();
        return new DashboardStatsResponse(patients.count(), appointmentsToday, availableBeds, revenue, pendingLabs, lowStock, pendingInvites);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> notifications() {
        return notifications.findAll().stream().map(mapper::toNotification).toList();
    }

    public void deleteNotification(Long id) {
        if (!notifications.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found.");
        }
        notifications.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<WardResponse> listWards() {
        return wards.findAll().stream().map(mapper::toWard).toList();
    }

    @Transactional(readOnly = true)
    public List<BedResponse> listBeds() {
        return beds.findAll().stream().map(mapper::toBed).toList();
    }

    @Transactional(readOnly = true)
    public ReportSummaryResponse reportSummary() {
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

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
