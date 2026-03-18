package com.qiromanager.qiromanager_backend.api.patients;

import com.qiromanager.qiromanager_backend.api.common.PageResponse;
import com.qiromanager.qiromanager_backend.application.patients.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patients", description = "Create, read, update and manage patient assignments. Requires JWT.")
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

    @Operation(summary = "Create a new patient (auto-assigned to the calling therapist)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
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

    @Operation(summary = "List all active patients (paginated). Use assignedToMe=true to filter by current therapist.")
    @ApiResponse(responseCode = "200", description = "Paginated list of patients")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<PatientResponse>> getAllPatients(
            @RequestParam(required = false) Boolean assignedToMe,
            @PageableDefault(size = 20, sort = "fullName") Pageable pageable
    ) {
        log.info("Request received: List patients (AssignedToMe filter: {}, page: {}, size: {})",
                assignedToMe, pageable.getPageNumber(), pageable.getPageSize());

        Page<PatientResponse> patients = listPatientsUseCase.execute(assignedToMe, pageable);

        log.debug("Returning page {}/{} ({} patients)", pageable.getPageNumber(),
                patients.getTotalPages(), patients.getNumberOfElements());
        return ResponseEntity.ok(PageResponse.from(patients));
    }

    @Operation(summary = "Get patient details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        log.info("Request received: Get patient details for ID: {}", id);

        PatientResponse response = getPatientByIdUseCase.execute(id);

        log.debug("Patient retrieved: {}", response.getFullName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update patient data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
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

    @Operation(summary = "Activate or deactivate a patient (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
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

    @Operation(summary = "Search patients by full name (paginated)")
    @ApiResponse(responseCode = "200", description = "Paginated search results")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<PageResponse<PatientResponse>> searchPatients(
            @RequestParam String query,
            @PageableDefault(size = 20, sort = "fullName") Pageable pageable
    ) {
        log.info("Request received: Search patients with query: '{}' (page: {}, size: {})",
                query, pageable.getPageNumber(), pageable.getPageSize());

        Page<PatientResponse> results = searchPatientsUseCase.execute(query, pageable);

        log.debug("Search found {} matching patients", results.getTotalElements());
        return ResponseEntity.ok(PageResponse.from(results));
    }

    @Operation(summary = "Assign the calling therapist to a patient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assigned successfully (idempotent)"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<PatientResponse> assignPatient(@PathVariable Long id) {
        log.info("Request received: Assign patient ID: {} to current user", id);

        PatientResponse response = assignPatientUseCase.execute(id);

        log.debug("Patient ID: {} assigned successfully", id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove the calling therapist from a patient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unassigned successfully (no-op if not assigned)"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}/assign")
    public ResponseEntity<PatientResponse> unassignPatient(@PathVariable Long id) {
        log.info("Request received: Unassign patient ID: {} from current user", id);

        PatientResponse response = unassignPatientUseCase.execute(id);

        log.debug("Patient ID: {} unassigned successfully", id);
        return ResponseEntity.ok(response);
    }
}
