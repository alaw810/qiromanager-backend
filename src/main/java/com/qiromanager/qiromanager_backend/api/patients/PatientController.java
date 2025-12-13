package com.qiromanager.qiromanager_backend.api.patients;

import com.qiromanager.qiromanager_backend.application.patients.CreatePatientUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody CreatePatientRequest request
    ) {
        PatientResponse response = createPatientUseCase.execute(request);
        return ResponseEntity.status(201).body(response);
    }
}
