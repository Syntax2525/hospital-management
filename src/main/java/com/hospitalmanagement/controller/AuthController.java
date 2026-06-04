package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.AuthLoginRequest;
import com.hospitalmanagement.dto.AuthSessionResponse;
import com.hospitalmanagement.response.ApiResponse;
import com.hospitalmanagement.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {
    private final HospitalService service;

    public AuthController(HospitalService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthSessionResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.login(request)));
    }
}
