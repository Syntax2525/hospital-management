package com.hospitalmanagement.service;

import com.hospitalmanagement.dto.*;

import java.util.List;

public interface HospitalService {
    AuthSessionResponse login(AuthLoginRequest request);

    List<PatientSummaryResponse> listPatients();

    List<PatientDetailResponse> listPatientDetails();

    PatientDetailResponse getPatient(Long id);

    PatientDetailResponse registerPatient(PatientRegistrationRequest request);

    PatientDetailResponse updatePatient(Long id, PatientRegistrationRequest request);

    void deletePatient(Long id);

    AppointmentRequest bookAppointment(AppointmentRequest request);

    List<AppointmentResponse> listAppointments();

    void deleteAppointment(Long id);

    TriageRecordRequest saveTriage(TriageRecordRequest request);

    ConsultationRequest saveConsultation(ConsultationRequest request);

    LabOrderResponse createLabOrder(LabOrderRequest request);

    List<LabOrderResponse> listLabOrders();

    LabResultResponse publishDemoResult(Long orderItemId);

    InvoiceResponse createInvoice(InvoiceRequest request);

    List<InvoiceResponse> listInvoices();

    InvoiceResponse recordPayment(PaymentRequest request);

    List<MedicationResponse> listMedications();

    MedicationResponse createMedication(MedicationRequest request);

    MedicationResponse updateMedication(Long id, MedicationRequest request);

    void deleteMedication(Long id);

    List<PrescriptionQueueResponse> prescriptionQueue();

    PharmacyDispenseRequest dispense(PharmacyDispenseRequest request);

    StaffUserResponse createStaff(StaffUserCreateRequest request);

    StaffUserResponse updateStaff(Long id, StaffUserUpdateRequest request);

    List<StaffUserResponse> listStaff();

    void deleteStaff(Long id);

    DashboardStatsResponse dashboardStats();

    List<NotificationResponse> notifications();

    void deleteNotification(Long id);

    List<WardResponse> listWards();

    List<BedResponse> listBeds();

    ReportSummaryResponse reportSummary();

    HospitalSettingsResponse hospitalSettings();
}
