package com.qiromanager.qiromanager_backend.api.treatments;

import com.qiromanager.qiromanager_backend.api.mappers.TreatmentSessionMapper;
import com.qiromanager.qiromanager_backend.application.treatments.CreateTreatmentSessionUseCase;
import com.qiromanager.qiromanager_backend.application.treatments.GetPatientTreatmentSessionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Treatment Sessions", description = "Log and retrieve treatment sessions for a patient. Requires JWT.")
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Slf4j
public class TreatmentSessionController {

    private final CreateTreatmentSessionUseCase createTreatmentSessionUseCase;
    private final GetPatientTreatmentSessionsUseCase getPatientTreatmentSessionsUseCase;
    private final TreatmentSessionMapper treatmentSessionMapper;

    @Operation(summary = "Log a new treatment session for a patient")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Session logged successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PostMapping("/{patientId}/sessions")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TreatmentSessionResponse> createSession(
            @PathVariable Long patientId,
            @Valid @RequestBody CreateTreatmentSessionRequest request
    ) {
        log.info("Request received: Log Treatment Session for Patient ID: {} on date {}",
                patientId, request.getSessionDate());

        TreatmentSessionResponse response = createTreatmentSessionUseCase.execute(patientId, request);

        log.debug("Treatment Session logged successfully. ID: {}", response.getId());
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Get all treatment sessions for a patient")
    @ApiResponse(responseCode = "200", description = "List of treatment sessions")
    @GetMapping("/{patientId}/sessions")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<TreatmentSessionResponse>> getSessionsByPatient(@PathVariable Long patientId) {
        log.info("Request received: Fetch Treatment Sessions for Patient ID: {}", patientId);

        var sessions = getPatientTreatmentSessionsUseCase.execute(patientId);

        log.debug("Retrieved {} sessions for Patient ID: {}", sessions.size(), patientId);
        return ResponseEntity.ok(sessions.stream()
                .map(treatmentSessionMapper::toResponse)
                .toList());
    }
}