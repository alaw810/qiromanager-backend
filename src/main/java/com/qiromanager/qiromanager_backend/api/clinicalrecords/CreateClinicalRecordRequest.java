package com.qiromanager.qiromanager_backend.api.clinicalrecords;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateClinicalRecordRequest {

    @NotNull(message = "Record type is required")
    private RecordType type;

    @NotBlank(message = "Content cannot be empty")
    private String content;
}