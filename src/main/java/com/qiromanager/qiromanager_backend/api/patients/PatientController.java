package com.qiromanager.qiromanager_backend.api.patients;

import com.qiromanager.qiromanager_backend.application.patients.CreatePatientUseCase;
import com.qiromanager.qiromanager_backend.application.patients.ListPatientsUseCase;
import com.qiromanager.qiromanager_backend.application.patients.UpdatePatientStatusUseCase;
import com.qiromanager.qiromanager_backend.application.patients.UpdatePatientUseCase;
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
    private final UpdatePatientUseCase updatePatientUseCase;
    private final UpdatePatientStatusUseCase updatePatientStatusUseCase;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody CreatePatientRequest request
    ) {
        PatientResponse response = createPatientUseCase.execute(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> response = listPatientsUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request
    ) {
        PatientResponse response = updatePatientUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponse> updatePatientStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientStatusRequest request
    ) {
        return ResponseEntity.ok(updatePatientStatusUseCase.execute(id, request));
    }

}
