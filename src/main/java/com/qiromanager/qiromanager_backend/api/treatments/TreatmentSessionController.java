package com.qiromanager.qiromanager_backend.api.treatments;

import com.qiromanager.qiromanager_backend.api.mappers.TreatmentSessionMapper;
import com.qiromanager.qiromanager_backend.application.treatments.CreateTreatmentSessionUseCase;
import com.qiromanager.qiromanager_backend.application.treatments.DeleteTreatmentSessionUseCase;
import com.qiromanager.qiromanager_backend.application.treatments.GetPatientTreatmentSessionsUseCase;
import com.qiromanager.qiromanager_backend.application.treatments.GetTreatmentSessionByIdUseCase;
import com.qiromanager.qiromanager_backend.application.treatments.UpdateTreatmentSessionUseCase;
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
    private final GetTreatmentSessionByIdUseCase getTreatmentSessionByIdUseCase;
    private final UpdateTreatmentSessionUseCase updateTreatmentSessionUseCase;
    private final DeleteTreatmentSessionUseCase deleteTreatmentSessionUseCase;
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

    @Operation(summary = "Get a specific treatment session by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session found"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @GetMapping("/{patientId}/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TreatmentSessionResponse> getSessionById(
            @PathVariable Long patientId,
            @PathVariable Long sessionId
    ) {
        log.info("Request received: Fetch Treatment Session ID: {} for Patient ID: {}", sessionId, patientId);
        TreatmentSessionResponse response = getTreatmentSessionByIdUseCase.execute(patientId, sessionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a specific treatment session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @PutMapping("/{patientId}/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TreatmentSessionResponse> updateSession(
            @PathVariable Long patientId,
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateTreatmentSessionRequest request
    ) {
        log.info("Request received: Update Treatment Session ID: {} for Patient ID: {}", sessionId, patientId);
        TreatmentSessionResponse response = updateTreatmentSessionUseCase.execute(patientId, sessionId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a specific treatment session")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Session deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @DeleteMapping("/{patientId}/sessions/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long patientId,
            @PathVariable Long sessionId
    ) {
        log.info("Request received: Delete Treatment Session ID: {} for Patient ID: {}", sessionId, patientId);
        deleteTreatmentSessionUseCase.execute(patientId, sessionId);
        return ResponseEntity.noContent().build();
    }
}