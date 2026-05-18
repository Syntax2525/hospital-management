package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.InvoiceRequest;
import com.hospitalmanagement.dto.InvoiceResponse;
import com.hospitalmanagement.dto.PaymentRequest;
import com.hospitalmanagement.response.ApiResponse;
import com.hospitalmanagement.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {
    private final HospitalService service;

    public BillingController(HospitalService service) {
        this.service = service;
    }

    @GetMapping("/invoices")
    public ApiResponse<List<InvoiceResponse>> invoices() {
        return ApiResponse.ok(service.listInvoices());
    }

    @PostMapping("/invoices")
    public ResponseEntity<ApiResponse<InvoiceResponse>> invoice(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.createInvoice(request)));
    }

    @PostMapping("/payments")
    public ApiResponse<InvoiceResponse> payment(@Valid @RequestBody PaymentRequest request) {
        return ApiResponse.ok(service.recordPayment(request));
    }
}
