package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.*;
import com.hospitalmanagement.response.ApiResponse;
import com.hospitalmanagement.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {
    private final HospitalService service;

    public AdminController(HospitalService service) {
        this.service = service;
    }

    @GetMapping("/dashboard/stats")
    public ApiResponse<DashboardStatsResponse> stats() {
        return ApiResponse.ok(service.dashboardStats());
    }

    @GetMapping("/notifications")
    public ApiResponse<List<NotificationResponse>> notifications() {
        return ApiResponse.ok(service.notifications());
    }

    @GetMapping("/pharmacy/medications")
    public ApiResponse<List<MedicationResponse>> medications() {
        return ApiResponse.ok(service.listMedications());
    }

    @PostMapping("/pharmacy/medications")
    public ResponseEntity<ApiResponse<MedicationResponse>> medication(@Valid @RequestBody MedicationRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.createMedication(request)));
    }

    @PutMapping("/pharmacy/medications/{id}")
    public ApiResponse<MedicationResponse> medication(@PathVariable Long id, @Valid @RequestBody MedicationRequest request) {
        return ApiResponse.ok(service.updateMedication(id, request));
    }

    @DeleteMapping("/pharmacy/medications/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedication(@PathVariable Long id) {
        service.deleteMedication(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/pharmacy/prescriptions")
    public ApiResponse<List<PrescriptionQueueResponse>> prescriptionQueue() {
        return ApiResponse.ok(service.prescriptionQueue());
    }

    @PostMapping("/pharmacy/dispenses")
    public ApiResponse<PharmacyDispenseRequest> dispense(@Valid @RequestBody PharmacyDispenseRequest request) {
        return ApiResponse.ok(service.dispense(request));
    }

    @GetMapping("/users")
    public ApiResponse<List<StaffUserResponse>> users() {
        return ApiResponse.ok(service.listStaff());
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<StaffUserResponse>> user(@Valid @RequestBody StaffUserCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.createStaff(request)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        service.deleteStaff(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        service.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/reports/summary")
    public ApiResponse<ReportSummaryResponse> reportSummary() {
        return ApiResponse.ok(service.reportSummary());
    }
}
