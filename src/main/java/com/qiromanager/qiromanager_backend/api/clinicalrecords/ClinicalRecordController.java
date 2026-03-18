package com.qiromanager.qiromanager_backend.api.clinicalrecords;

import com.qiromanager.qiromanager_backend.application.clinicalrecords.CreateClinicalRecordUseCase;
import com.qiromanager.qiromanager_backend.application.clinicalrecords.GetPatientClinicalRecordsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Clinical Records", description = "Create and retrieve clinical history records for a patient. Requires JWT.")
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Slf4j
public class ClinicalRecordController {

    private final CreateClinicalRecordUseCase createClinicalRecordUseCase;
    private final GetPatientClinicalRecordsUseCase getPatientClinicalRecordsUseCase;

    @Operation(summary = "Create a clinical record for a patient (supports optional file attachment)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Clinical record created"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PostMapping(value = "/{patientId}/clinical-records", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ClinicalRecordResponse> createClinicalRecord(
            @PathVariable Long patientId,
            @RequestPart("request") @Valid CreateClinicalRecordRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        boolean hasFile = file != null && !file.isEmpty();
        log.info("Request received: Create Clinical Record for Patient ID: {} (Type: {}, HasFile: {})",
                patientId, request.getType(), hasFile);

        ClinicalRecordResponse response = createClinicalRecordUseCase.execute(patientId, request, file);

        log.debug("Clinical Record created successfully. ID: {}", response.getId());
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Get all clinical records for a patient")
    @ApiResponse(responseCode = "200", description = "List of clinical records")
    @GetMapping("/{patientId}/clinical-records")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ClinicalRecordResponse>> getClinicalRecords(
            @PathVariable Long patientId
    ) {
        log.info("Request received: Fetch Clinical History for Patient ID: {}", patientId);

        List<ClinicalRecordResponse> records = getPatientClinicalRecordsUseCase.execute(patientId);

        log.debug("Retrieved {} clinical records for Patient ID: {}", records.size(), patientId);
        return ResponseEntity.ok(records);
    }
}