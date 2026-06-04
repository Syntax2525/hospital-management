package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.*;
import com.hospitalmanagement.response.ApiResponse;
import com.hospitalmanagement.service.HospitalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/clinical")
public class ClinicalController {
    private final HospitalService service;

    public ClinicalController(HospitalService service) {
        this.service = service;
    }

    @PostMapping("/triage")
    public ResponseEntity<ApiResponse<TriageRecordRequest>> triage(@Valid @RequestBody TriageRecordRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.saveTriage(request)));
    }

    @PostMapping("/consultations")
    public ResponseEntity<ApiResponse<ConsultationRequest>> consultation(@Valid @RequestBody ConsultationRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.saveConsultation(request)));
    }

    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<AppointmentRequest>> appointment(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.bookAppointment(request)));
    }

    @GetMapping("/appointments")
    public ApiResponse<List<AppointmentResponse>> appointments() {
        return ApiResponse.ok(service.listAppointments());
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable @Positive Long id) {
        service.deleteAppointment(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/lab-orders")
    public ResponseEntity<ApiResponse<LabOrderResponse>> labOrder(@Valid @RequestBody LabOrderRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.createLabOrder(request)));
    }

    @GetMapping("/lab-orders")
    public ApiResponse<List<LabOrderResponse>> labOrders() {
        return ApiResponse.ok(service.listLabOrders());
    }

    @PostMapping("/lab-order-items/{id}/result")
    public ApiResponse<LabResultResponse> publishResult(@PathVariable @Positive Long id) {
        return ApiResponse.ok(service.publishDemoResult(id));
    }

    @GetMapping("/wards")
    public ApiResponse<List<WardResponse>> wards() {
        return ApiResponse.ok(service.listWards());
    }

    @GetMapping("/beds")
    public ApiResponse<List<BedResponse>> beds() {
        return ApiResponse.ok(service.listBeds());
    }
}
