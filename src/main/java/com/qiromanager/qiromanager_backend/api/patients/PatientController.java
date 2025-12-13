package com.qiromanager.qiromanager_backend.api.patients;

import com.qiromanager.qiromanager_backend.application.patients.CreatePatientUseCase;
import com.qiromanager.qiromanager_backend.application.patients.ListPatientsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;
    private final ListPatientsUseCase listPatientsUseCase;

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
}
