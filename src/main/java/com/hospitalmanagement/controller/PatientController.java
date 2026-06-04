package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.PatientDetailResponse;
import com.hospitalmanagement.dto.PatientRegistrationRequest;
import com.hospitalmanagement.dto.PatientSummaryResponse;
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
@RequestMapping("/api/patients")
public class PatientController {
    private final HospitalService service;

    public PatientController(HospitalService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<PatientDetailResponse>> list() {
        return ApiResponse.ok(service.listPatientDetails());
    }

    @GetMapping("/{id}")
    public ApiResponse<PatientDetailResponse> get(@PathVariable @Positive Long id) {
        return ApiResponse.ok(service.getPatient(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PatientDetailResponse>> create(@Valid @RequestBody PatientRegistrationRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.registerPatient(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<PatientDetailResponse> update(@PathVariable @Positive Long id, @Valid @RequestBody PatientRegistrationRequest request) {
        return ApiResponse.ok(service.updatePatient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable @Positive Long id) {
        service.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
