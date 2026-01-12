package com.qiromanager.qiromanager_backend.api.patients;

import com.qiromanager.qiromanager_backend.application.patients.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Slf4j
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
        log.info("Request received: Create new patient (Name: {})", request.getFullName());

        PatientResponse response = createPatientUseCase.execute(request);

        log.debug("Patient created successfully with ID: {}", response.getId());
        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients(
            @RequestParam(required = false) Boolean assignedToMe
    ) {
        log.info("Request received: List patients (AssignedToMe filter: {})", assignedToMe);

        List<PatientResponse> patients = listPatientsUseCase.execute(assignedToMe);

        log.debug("Returning {} patients", patients.size());
        return ResponseEntity.ok(patients);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        log.info("Request received: Get patient details for ID: {}", id);

        PatientResponse response = getPatientByIdUseCase.execute(id);

        log.debug("Patient retrieved: {}", response.getFullName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request
    ) {
        log.info("Request received: Update patient ID: {}", id);

        PatientResponse response = updatePatientUseCase.execute(id, request);

        log.debug("Patient ID: {} updated successfully", id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PatientResponse> updatePatientStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientStatusRequest request
    ) {
        log.info("Request received: Update status for patient ID: {} to active={}", id, request.getActive());

        PatientResponse response = updatePatientStatusUseCase.execute(id, request);

        log.debug("Patient ID: {} status updated", id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchPatients(@RequestParam String query) {
        log.info("Request received: Search patients with query: '{}'", query);

        List<PatientResponse> results = searchPatientsUseCase.execute(query);

        log.debug("Search found {} matching patients", results.size());
        return ResponseEntity.ok(results);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<PatientResponse> assignPatient(@PathVariable Long id) {
        log.info("Request received: Assign patient ID: {} to current user", id);

        PatientResponse response = assignPatientUseCase.execute(id);

        log.debug("Patient ID: {} assigned successfully", id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}/assign")
    public ResponseEntity<PatientResponse> unassignPatient(@PathVariable Long id) {
        log.info("Request received: Unassign patient ID: {} from current user", id);

        PatientResponse response = unassignPatientUseCase.execute(id);

        log.debug("Patient ID: {} unassigned successfully", id);
        return ResponseEntity.ok(response);
    }
}