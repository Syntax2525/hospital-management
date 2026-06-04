package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.*;
import com.hospitalmanagement.model.Allergy;
import com.hospitalmanagement.model.Appointment;
import com.hospitalmanagement.model.Bed;
import com.hospitalmanagement.model.Invoice;
import com.hospitalmanagement.model.LabOrder;
import com.hospitalmanagement.model.LabResult;
import com.hospitalmanagement.model.Medication;
import com.hospitalmanagement.model.Notification;
import com.hospitalmanagement.model.Patient;
import com.hospitalmanagement.model.Prescription;
import com.hospitalmanagement.model.PrescriptionItem;
import com.hospitalmanagement.model.StaffUser;
import com.hospitalmanagement.model.Ward;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HospitalMapper {

    public PatientSummaryResponse toSummary(Patient patient) {
        return new PatientSummaryResponse(
                patient.getId(),
                patient.getPatientNumber(),
                patient.getFullName(),
                patient.getGender(),
                patient.getAge(),
                patient.getPhone(),
                patient.getPriority()
        );
    }

    public PatientDetailResponse toDetail(Patient patient) {
        List<String> allergies = patient.getAllergies().stream().map(Allergy::getName).sorted().toList();
        return new PatientDetailResponse(
                patient.getId(),
                patient.getPatientNumber(),
                patient.getFullName(),
                patient.getGender(),
                patient.getAge(),
                patient.getPhone(),
                patient.getAddress(),
                patient.getInsuranceProvider(),
                patient.getBloodGroup(),
                patient.getPriority(),
                patient.getStatus(),
                patient.getLastVisitDate(),
                patient.getPhotoUrl(),
                allergies
        );
    }

    public StaffUserResponse toStaff(StaffUser user) {
        return new StaffUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getDepartment() == null ? null : user.getDepartment().getName(),
                user.getPhone()
        );
    }

    public AppointmentResponse toAppointment(Appointment appointment) {
        StaffUser doctor = appointment.getAssignedDoctor();
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getFullName(),
                doctor == null ? null : doctor.getId(),
                doctor == null ? null : doctor.getFullName(),
                appointment.getScheduledAt(),
                appointment.getType(),
                appointment.getStatus(),
                appointment.getReason()
        );
    }

    public MedicationResponse toMedication(Medication medication) {
        return new MedicationResponse(
                medication.getId(),
                medication.getName(),
                medication.getStrength(),
                medication.getDosageForm(),
                medication.getStockQuantity(),
                medication.getReorderLevel(),
                medication.getUnitPrice(),
                medication.getInventoryStatus()
        );
    }

    public PrescriptionQueueResponse toPrescriptionQueue(PrescriptionItem item) {
        Medication medication = item.getMedication();
        Prescription prescription = item.getPrescription();
        return new PrescriptionQueueResponse(
                item.getId(),
                prescription.getPrescriptionNumber(),
                prescription.getPatient().getFullName(),
                medication.getName(),
                medication.getStrength(),
                item.getDosage(),
                item.getFrequency(),
                item.getQuantity(),
                item.getDispense() != null
        );
    }

    public WardResponse toWard(Ward ward) {
        long available = ward.getBeds().stream()
                .filter(bed -> bed.getStatus() == com.hospitalmanagement.enums.BedStatus.AVAILABLE)
                .count();
        return new WardResponse(
                ward.getId(),
                ward.getName(),
                ward.getFloor(),
                ward.getBeds().size(),
                available
        );
    }

    public BedResponse toBed(Bed bed) {
        Ward ward = bed.getWard();
        return new BedResponse(
                bed.getId(),
                bed.getBedNumber(),
                bed.getStatus(),
                ward == null ? null : ward.getId(),
                ward == null ? null : ward.getName(),
                ward == null ? null : ward.getFloor()
        );
    }

    public LabOrderResponse toLabOrder(LabOrder order) {
        List<String> tests = order.getItems().stream()
                .map(item -> item.getLabTest().getName())
                .toList();
        return new LabOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getPatient().getId(),
                order.getPatient().getFullName(),
                order.getRequestedAt(),
                order.getStatus(),
                tests
        );
    }

    public LabResultResponse toLabResult(LabResult result) {
        return new LabResultResponse(
                result.getId(),
                result.getLabOrderItem().getLabTest().getName(),
                result.getResultValues(),
                result.getInterpretation(),
                result.getPublishedAt()
        );
    }

    public InvoiceResponse toInvoice(Invoice invoice) {
        List<InvoiceLineItemDto> items = invoice.getLineItems().stream()
                .map(item -> new InvoiceLineItemDto(item.getLabel(), item.getQuantity(), item.getUnitAmount(), item.getLineTotal()))
                .toList();
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getPatient().getPatientNumber(),
                invoice.getPatient().getFullName(),
                invoice.getIssuedAt(),
                invoice.getTotalAmount(),
                invoice.getStatus(),
                items
        );
    }

    public NotificationResponse toNotification(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }
}
