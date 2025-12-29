package com.qiromanager.qiromanager_backend.api.treatments;

import com.qiromanager.qiromanager_backend.application.treatments.CreateTreatmentSessionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class TreatmentSessionController {

    private final CreateTreatmentSessionUseCase createTreatmentSessionUseCase;

    @PostMapping("/{patientId}/sessions")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TreatmentSessionResponse> createSession(
            @PathVariable Long patientId,
            @Valid @RequestBody CreateTreatmentSessionRequest request
    ) {
        TreatmentSessionResponse response = createTreatmentSessionUseCase.execute(patientId, request);
        return ResponseEntity.status(201).body(response);
    }
}