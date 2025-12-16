package com.qiromanager.qiromanager_backend.api.patients;

import com.qiromanager.qiromanager_backend.application.patients.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;
    private final ListPatientsUseCase listPatientsUseCase;
    private final GetPatientByIdUseCase getPatientByIdUseCase;
    private final UpdatePatientUseCase updatePatientUseCase;
    private final UpdatePatientStatusUseCase updatePatientStatusUseCase;
    private final SearchPatientsUseCase searchPatientsUseCase;
    private final AssignPatientUseCase assignPatientUseCase;
    private final UnassignPatientUseCase unassignPatientUseCase;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody CreatePatientRequest request
    ) {
        return ResponseEntity.status(201).body(createPatientUseCase.execute(request));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(listPatientsUseCase.execute());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(getPatientByIdUseCase.execute(id));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request
    ) {
        return ResponseEntity.ok(updatePatientUseCase.execute(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PatientResponse> updatePatientStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientStatusRequest request
    ) {
        return ResponseEntity.ok(updatePatientStatusUseCase.execute(id, request));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchPatients(@RequestParam String query) {
        return ResponseEntity.ok(searchPatientsUseCase.execute(query));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<PatientResponse> assignPatient(@PathVariable Long id) {
        return ResponseEntity.ok(assignPatientUseCase.execute(id));
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}/assign")
    public ResponseEntity<PatientResponse> unassignPatient(@PathVariable Long id) {
        return ResponseEntity.ok(unassignPatientUseCase.execute(id));
    }
}

