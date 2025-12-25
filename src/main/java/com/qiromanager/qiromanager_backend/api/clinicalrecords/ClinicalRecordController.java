package com.qiromanager.qiromanager_backend.api.clinicalrecords;

import com.qiromanager.qiromanager_backend.application.clinicalrecords.CreateClinicalRecordUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class ClinicalRecordController {

    private final CreateClinicalRecordUseCase createClinicalRecordUseCase;

    @PostMapping(value = "/{patientId}/clinical-records", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ClinicalRecordResponse> createClinicalRecord(
            @PathVariable Long patientId,
            @RequestPart("request") @Valid CreateClinicalRecordRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        ClinicalRecordResponse response = createClinicalRecordUseCase.execute(patientId, request, file);
        return ResponseEntity.status(201).body(response);
    }
}